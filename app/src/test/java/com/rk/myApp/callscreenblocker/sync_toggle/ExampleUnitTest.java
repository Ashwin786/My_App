package com.rk.myApp.callscreenblocker.sync_toggle;

import android.util.Base64;
import android.util.Log;

import com.rk.myApp.callscreenblocker.sync_toggle.A;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {

            test();
//        date_check();
//        override();
        time_check();
        if (1 == 1)
            return;
        forcheck();

        check_decryption();
//        assertEquals(4, 2 + 2);
        String a = "Ramesh";
        String b = "Ram";
//        String b = new String("ramesh");
        /*StringBuilder A =new StringBuilder();
        A.append("1");
        StringBuilder b =new StringBuilder();
        b.append("1");*/
//        if(A.contains(b))
//            System.out.println(true);
//        else
//            System.out.println(false);
        HashMap<String, String> map = new HashMap<>();
        map.put("1", "ramesh");
        map.put("2", "ramesh");
//        System.out.println(map.get("1").hashCode());
//        System.out.println(map.get("2").hashCode());
//        Set<String> what = map.keySet();
//        Iterator<String> gett = what.iterator();
//        while (gett.hasNext()){
//            System.out.println(gett.t);
//            gett.next();
//        }
        for (String key : map.keySet()) {
            System.out.println(key.hashCode());
        }

    }

    private void time_check() {
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
        Date last_hit_date = null;
        Date last_retry_hit_date = null;
        Date current_date = null;
        try {
            current_date = sdf.parse("2019-04-25 18:02:00");
            last_hit_date = sdf.parse("2019-04-25 18:00:00");
            last_retry_hit_date = sdf.parse("2019-04-25 18:01:55");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long current_time = current_date.getTime();
        long last_hit_time = last_hit_date.getTime();
        int frequency = 20;
        int duration = 120;

        long total_duration = 120 + duration;
        long balance_time = (current_time - last_hit_time) / 1000;

        if (balance_time < total_duration) {
            long last_retry_hit_time = last_retry_hit_date.getTime();
//            long last_retry_hit_time = 0;
            long retry_balance_time = (current_time - last_retry_hit_time) / 1000;
            if (retry_balance_time < frequency) {
                long wait_time = frequency - retry_balance_time;
                System.out.println("wait time = " + wait_time);
            }
            System.out.println("Call api");
        }
    }

    private void test() {
        String i = "A";
        if (i != null && (i.equals("B") || i.equals("A"))) {
            System.out.println(i);
        } else
            System.out.println(i + "");
    }

    private void date_check() {
        /*Scanner input = new Scanner(System.in);
        String myString = input.next();
        Log.e("Time_check","Text entered = " + myString);*/
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//        sdf.format(new Date());
        String hour = "17:00:00";
        String[] openingHour1 = hour.split(":");
        String opHours1 = "AM";
        int opens1 = Integer.parseInt(openingHour1[0]);
        if (opens1 > 12) {
            opHours1 = "PM";
            opens1 = opens1 % 12;
        }


        String operationHour1 = opens1 + ":" + openingHour1[1];
        System.out.println("Time_check " + operationHour1);
        /*Calendar cal = Calendar.getInstance();

        String in_time = "20:00:00";
        if (in_time != null && !in_time.isEmpty()) {
            String[] intimer = in_time.split(":");
            System.out.println("Current hour" + cal.get(Calendar.HOUR_OF_DAY));
            if (cal.get(Calendar.HOUR_OF_DAY) < Integer.parseInt(intimer[0]))
                System.out.println("Time_check " + "Yes");
            else
                System.out.println("Time_check " + "No");*/


            /*cal.set(Calendar.HOUR,Integer.parseInt(intimer[0]));
            sdf = new SimpleDateFormat("HH:mm:ss");
            try {
                Date time = sdf.parse(in_time);
                Date current_time = new Date();

                System.out.println("Time_check "+ "Given time " + time);
                System.out.println("Time_check "+ "current_time time " + current_time);
                boolean value = current_time.after(time);
                System.out.println("Time_check "+ "value " + value);
        } catch(ParseException e){
            e.printStackTrace();
        }*/

    }


    public void patterndemo() {

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j <= i; j++) {
                System.out.print("*");
            }

            System.out.println();
        }

    }

    private void override() {
        B b = new B();
        b.getClassName();
    }

    private void forcheck() {
        for (int i = 0; i < 3; i++) {
            System.out.print(" * ");
            for (int j = i; j >= 0; j--) {
                System.out.print(" ");
            }
            System.out.print("\n");
        }
    }

    private void check_decryption() {

    }

    String Decrypt(String text, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] keyBytes = new byte[16];
        byte[] b = key.getBytes("UTF-8");
        int len = b.length;
        if (len > keyBytes.length) len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        Log.e("Decrypt", "data to be decrypted..." + text);
        byte[] results = cipher.doFinal(text.getBytes("UTF-8"));
        return new String(Base64.decode(results, Base64.DEFAULT), "UTF-8");
    }
}