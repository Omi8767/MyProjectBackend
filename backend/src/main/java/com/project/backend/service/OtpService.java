package com.project.backend.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {
    private final ConcurrentHashMap<String,String> otpStore= new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String,Long> expiryStore = new ConcurrentHashMap<>();

    public String generateOtp(String email){
        String otp = String.valueOf((int)(Math.random()*900000)+10000);
        otpStore.put(email,otp);
        expiryStore.put(email,System.currentTimeMillis()+5*60*1000);
        return otp;
    }

    public boolean verifyOtp(String email,String otp){
        Long expiry = expiryStore.get(email);

        if(expiry == null || System.currentTimeMillis() > expiry){
            return false;
        }

        return otp.equals(otpStore.get(email));
    }

    public void clearOtp(String email){
        otpStore.remove(email);
        expiryStore.remove(email);
    }

}
