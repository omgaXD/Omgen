package com.omga.omgen.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;

public class StaticHelper {
    public static <T, U> boolean isListAInListB(List<T> listA, List<U> listB, BiPredicate<T, U> predicate) {
        ArrayList<U> tempListB = (ArrayList<U>)new ArrayList<>(listB).clone();
        for (T element : listA) {
            boolean found = false;
            for (U check : tempListB) {
                if (predicate.test(element, check)) {
                    tempListB.remove(check);
                    found = true; break;
                }
            }
            if (!found) return false;
        }
        return true;
    }
}
