package com.agilone.impl.test;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestCollectionDifferences {

    @Test
    public void testFindDifferences() {
        List<String> list1 = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        List<String> list3 = new ArrayList<>();

        list1.add("ABC");
        list1.add("DEF");
        list1.add("GHI");
        list1.add("MNO");

        list2.add("ABC");
        list2.add("DEF");
        list2.add("GHI");
        list2.add("MNO");

        list3.add("ABC");
        list3.add("DEF");
        list3.add("GHI");
        list3.add("JKL");

        System.out.println( "Finding differences.");
        System.out.println( CollectionUtils.isEqualCollection( list1, list2));
        System.out.println( CollectionUtils.isEqualCollection( list1, list3));

        System.out.println( "intersection" + CollectionUtils.intersection( list1, list3));
        System.out.println( "disjunction" + CollectionUtils.disjunction( list1, list3));

        System.out.println( "list1, list3 substract" + CollectionUtils.subtract( list1, list3));
        System.out.println( "list3, list2 substract" + CollectionUtils.subtract( list3, list2));
        System.out.println( "list1, list2 substract" + CollectionUtils.subtract( list1, list2));
    }
}
