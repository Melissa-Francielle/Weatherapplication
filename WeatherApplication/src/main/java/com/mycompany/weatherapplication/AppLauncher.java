package com.mycompany.weatherapplication;


import javax.swing.SwingUtilities;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Melissa Francielle
 */
public class AppLauncher {
    public static void main (String[] args){
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
                //display our weather app GUI
                new WeatherAppGui().setVisible(true);
               
             //  System.out.println(WeatherApp.getLocationData("Tokyo"));
             
                System.out.println(WeatherApp.getCurrentTime());
            }
        });
    }
}
