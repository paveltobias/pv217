package pv217;

import java.util.Collection;
import java.util.stream.Collectors;

public class Utils {
    static String join(Collection<?> coll) {
        return String.join(
            ",",
            coll
                .stream()
                .map(v -> v.toString())
                .collect(Collectors.toList())
        );
    }
}
