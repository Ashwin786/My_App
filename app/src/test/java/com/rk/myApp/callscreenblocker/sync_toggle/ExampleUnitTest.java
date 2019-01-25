package com.rk.myApp.sync_toggle;

import android.util.Base64;
import android.util.Log;

import org.junit.Test;

import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
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
        HashMap<String,String> map = new HashMap<>();
        map.put("1","ramesh");
        map.put("2","ramesh");
//        System.out.println(map.get("1").hashCode());
//        System.out.println(map.get("2").hashCode());
//        Set<String> what = map.keySet();
//        Iterator<String> gett = what.iterator();
//        while (gett.hasNext()){
//            System.out.println(gett.t);
//            gett.next();
//        }
        for ( String key : map.keySet() ) {
            System.out.println( key.hashCode() );
        }

    }

    private void check_decryption() {

    }

    String Decrypt(String text, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] keyBytes= new byte[16];
        byte[] b= key.getBytes("UTF-8");
        int len= b.length;
        if (len > keyBytes.length) len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
        cipher.init(Cipher.DECRYPT_MODE,keySpec,ivSpec);
        Log.e("Decrypt", "data to be decrypted..."+text);
        byte[] results = cipher.doFinal(text.getBytes("UTF-8"));
        return new String(Base64.decode(results ,Base64.DEFAULT), "UTF-8");
    }
}