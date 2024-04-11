package com.mycompany.weatherapplication;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import org.json.simple.JSONObject;

public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;
    
    public WeatherAppGui(){
        //setup our GUI and add a title for the app
        super("Weather App");
        
        //Configure GUI to end the program's process once it has been closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        //set the size of our GUI (in pixels)
        setSize(450, 650);
        
        //load our GUI at the center of the screen
        setLocationRelativeTo(null);
        
        //make our layout manager null to manually position our components 
        //within the GUI
        setLayout(null);
        
        //prevent any resize of our GUI
        setResizable(false);
        
        addGuiComponents();
        
    }
    private void addGuiComponents(){
        //search field
        JTextField searchTextField = new JTextField();
        
        searchTextField.setBounds(15, 15, 351, 45);
        
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchTextField);
        
        
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);
        
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 40));
        
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);
        
        JLabel weatherConditionDesc = new JLabel ("Cloudy");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);
       
        
        JLabel hunidityImage= new JLabel(loadImage("src/assets/humidity.png"));
        hunidityImage.setBounds(15, 500, 74, 66);
        add(hunidityImage);
        
        JLabel hunidityText = new JLabel("<html><b>Hunidity</b> 100%</html>");
        hunidityText.setBounds(90, 500, 85, 55);
        hunidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(hunidityText);
        
        JLabel windsspeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windsspeedImage.setBounds(220, 500, 74, 66);
        add(windsspeedImage);
        
        JLabel windsspeedText = new JLabel("<html><b>Windspeed</b> 15km</html>");
        windsspeedText.setBounds(310, 500, 85, 55);
        windsspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windsspeedText);
        
        //search Button
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));
    
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                try {
                    String userInput = searchTextField.getText();
                    
                    // validate input - remove whitespace to ensure non-empty text
                    if(userInput.replaceAll("\\s", "").length() <= 0){
                        return;
                    }
                    
                    // retrieve weather data
                    weatherData = WeatherApp.getWeatherData(userInput);
                    
                    // update gui
                    
                    // update weather image
                    String weatherCondition = (String) weatherData.get("weather_condition");
                    
                    switch(weatherCondition){
                        case "Clear":
                            weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                            break;
                        case "Cloudy":
                            weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                            break;
                        case "Rain":
                            weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                            break;
                        case "Snow":
                            weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
                            break;
                    }
                    double temperature = (double) weatherData.get("temperature");
                    temperatureText.setText(temperature + "ºC");

                    weatherConditionDesc.setText((String) weatherData.get("weather_condition"));
                    
                    long humidity = (long) weatherData.get("humidity");
                    hunidityText.setText("<html><b>Humidity</b>: " + humidity + "%</html>");

                    double windSpeed = (double) weatherData.get("wind_speed");
                    windsspeedText.setText("<html><b>Wind Speed</b>: " + windSpeed + "km/h</html>");
                } catch (IOException ex) {
                    Logger.getLogger(WeatherAppGui.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
            }
        });
        add(searchButton);
        
        
        
    }
        
    private ImageIcon loadImage(String resourcePath){
        try{
            BufferedImage image = ImageIO.read(new File(resourcePath));
            
            return new ImageIcon(image);
        }catch(IOException e){
            e.printStackTrace();
            
        }
        System.out.println("Could not find resouce");
        return null;
    }
}
