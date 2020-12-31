#!/bin/bash

EMAIL_SVC=:8080
USER_SVC=:8081
COURSE_SVC=:8082
HOMEWORK_SVC=:8083

DB_HOST=localhost
DB_PORT=5432
DB_NAME=d
DB_USER=u
DB_PASS=p
DB_TABLES='person course assignment'
DB_IMPORTS='user-service course-service'

hit() {
    cmd="http -pb --pretty=none"
    for arg in "$@"; do
        cmd="$cmd \"$arg\""
    done
    echo -e "--> $cmd\n"
    res=$(eval "$cmd" | head -n 1)
    echo -e "<-- $res\n"
}

assert() {
    diff <(echo "$res") <(echo "$1")
    if [[ "$?" != 0 ]]; then
        echo 'Assertion failed'
        exit 1
    fi
}

# Clear SQL tables and re-import mock data.
export PGPASSWORD="$DB_PASS"
for table in $(echo $DB_TABLES); do
    psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -c "TRUNCATE $table RESTART IDENTITY CASCADE;"
done
for svc in $(echo $DB_IMPORTS); do
    psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -f "$svc/src/main/resources/import.sql"
done

# Get a teacher's JWT.
hit POST "$USER_SVC/auth/login" email=john@doe.io pass=heslo123
auth_teacher="Authorization: Bearer $res"

# Get a student's JWT.
hit POST "$USER_SVC/auth/login" email=alice@bob.io pass=heslo123
auth_student="Authorization: Bearer $res"

# Get courses as a teacher.
hit GET "$COURSE_SVC/courses" "$auth_teacher"
assert '[{"id":1,"name":"Service Oriented Architecture","studentIds":[]}]'

# Get courses as a student.
hit GET "$COURSE_SVC/courses" "$auth_student"
assert '[]'

# Change the course's name.
hit PATCH "$COURSE_SVC/courses/1" name=SOA "$auth_teacher"
assert '{"id":1,"name":"SOA","studentIds":[]}'

# Publish an assignment.
hit POST "$HOMEWORK_SVC/assignments" courseId:=1 description='Do something.' "$auth_teacher"
assert '{"id":1,"courseId":1,"description":"Do something.","solution":[]}'

# Check the assignment's persistence.
hit GET "$HOMEWORK_SVC/assignments" "$auth_teacher"
assert '[{"id":1,"courseId":1,"description":"Do something.","solution":[]}]'

# Check that an unregistered student cannot see the assignment.
hit GET "$HOMEWORK_SVC/assignments" "$auth_student"
assert '[]'

# Check the persistence of the changed name.
hit GET "$COURSE_SVC/courses" "$auth_teacher"
assert '[{"id":1,"name":"SOA","studentIds":[]}]'

# Register a student for the course.
hit PATCH "$COURSE_SVC/courses/1" studentIds:=[2] "$auth_teacher"
assert '{"id":1,"name":"SOA","studentIds":[2]}'

# Check the persistence of the student's registration.
hit GET "$COURSE_SVC/courses" "$auth_teacher"
assert '[{"id":1,"name":"SOA","studentIds":[2]}]'

# Check that the student can now see the course.
hit GET "$COURSE_SVC/courses" "$auth_student"
assert '[{"id":1,"name":"SOA","studentIds":[2]}]'

# Check that the student can now see the assignment.
hit GET "$HOMEWORK_SVC/assignments" "$auth_student"
assert '[{"id":1,"courseId":1,"description":"Do something.","solution":[]}]'

echo 'Everything ok!'
