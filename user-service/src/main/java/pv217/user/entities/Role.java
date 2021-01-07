package pv217.user.entities;

public enum Role {
    Teacher, Student;

    public String toString() {
        switch (this) {
            case Teacher:
                return "teacher";
            case Student:
                return "student";
            default:
                return null;
        }
    }
}
