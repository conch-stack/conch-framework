package com.nabob.conch.sample.gc;

/* GarbageCollection.java
 * Copyright (c) HerongYang.com. All Rights Reserved.
 */
public class GarbageCollection {
   public static void run() {
      int max = 10000;
      int min = 32;
      Object[] arr = new Object[min];
      Runtime rt = Runtime.getRuntime();
      System.out.println("Step/TotalMemory/FreeMemory/UsedMemory:");
      for (int m=0; m<max; m++) {
         for (int n=0; n<min-1; n++) arr[min-n-1] = arr[min-n-2];
         arr[0] = getOneMega();
         long total = rt.totalMemory();
         long free = rt.freeMemory();
         System.out.println((m+1)+"   "+total+"   "+free+"   "
            +(total-free));
         try {
            Thread.sleep(1000);
         } catch (InterruptedException e) {
            System.out.println("Interreupted...");
         }
      }
   }
   private static Object getOneMega() {
      Object[] lst = new Object[10];
      lst[0] = new long[256*128]; // 1/4 MB
      lst[1] = new int[256*256]; // 1/4 MB
      lst[2] = new double[256*128]; // 1/4 MB
      lst[3] = new float[64*256]; // 1/16 MB
      lst[4] = new byte[64*1024]; // 1/16 MB
      String[] l = new String[64*64]; // 1/16 MB
      for (int i=0; i<64*64; i++)
         l[i] = new String("12345678"); // 16B
      lst[5] = l;
      lst[6] = new char[64*512]; // 1/16 MB
      return lst;
   }
}