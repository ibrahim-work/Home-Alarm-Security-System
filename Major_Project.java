import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import java.io.IOException;

import org.firmata4j.ssd1306.MonochromeCanvas;
import org.firmata4j.ssd1306.SSD1306;
import org.firmata4j.I2CDevice;

public class Major_Project {

    static final byte I2C0 = 0x3c;

    public static void main(String[] args)
            throws InterruptedException, IOException {
        String myUSB = "COM4";
        IODevice myBoard = new FirmataDevice(myUSB);
        myBoard.start();
        myBoard.ensureInitializationIsDone();

        Pin MoistureSensor = myBoard.getPin(16);
        Pin Button = myBoard.getPin(6);
        Pin Buzzer = myBoard.getPin(5);
        Pin Potentiometer = myBoard.getPin(14);
        Pin SoundSensor = myBoard.getPin(16);
        Pin Red_LED = myBoard.getPin(4);
        Pin Green_LED = myBoard.getPin(13);
        Pin TemperatureSensor = myBoard.getPin(3);

        MoistureSensor.setMode(Pin.Mode.INPUT);
        Buzzer.setMode(Pin.Mode.PWM);
        Potentiometer.setMode(Pin.Mode.ANALOG);
        SoundSensor.setMode(Pin.Mode.ANALOG);
        Button.setMode(Pin.Mode.INPUT);
        Red_LED.setMode(Pin.Mode.OUTPUT);
        Green_LED.setMode(Pin.Mode.OUTPUT);
        TemperatureSensor.setMode(Pin.Mode.INPUT);

        I2CDevice i2cObject = myBoard.getI2CDevice((byte) 0x3C);
        SSD1306 oLED = new SSD1306(i2cObject, SSD1306.Size.SSD1306_128_64);
        oLED.init();

        System.out.println("Would you like to engage the Alarm System? Answer by turning the potentiometer (A0) towards your decision and pressing the button (D6) on the Arduino Board.");

        while (true) {

            oLED.getCanvas().drawString(0, 0, "ENGAGE ALARM SYSTEM? ");
            oLED.display();
            oLED.getCanvas().drawString(5, 40, "ON");
            oLED.display();
            oLED.getCanvas().drawString(80, 40, "OFF");
            oLED.display();

            if (Potentiometer.getValue() > 520 && Button.getValue() == 1) {

                oLED.getCanvas().drawRect(0, 35, 30, 20);
                oLED.getCanvas().drawRect(95, 35, 30, 20, MonochromeCanvas.Color.DARK);
                oLED.display();
                Thread.sleep(2000);
                oLED.getCanvas().drawString(0, 20, "ALARM ENGAGED");
                oLED.display();

                Thread.sleep(5000);

                if (MoistureSensor.getValue() < 750 && SoundSensor.getValue() >= 500 && TemperatureSensor.getValue() >= 1) {
                    System.out.println("WARNING...Intruder ALERT! Please disengage if false alarm! ");
                    oLED.getCanvas().drawString(0, 20, "ALERT! ALERT! ");
                    oLED.display();
                    Buzzer.setValue(1);
                    Red_LED.setValue(1);
                    Green_LED.setValue(1);
                }
                if (Button.getValue() == 1) {
                    oLED.clear();
                    oLED.getCanvas().drawString(0, 20, "ALARM DISENGAGED");
                    oLED.display();
                    Buzzer.setValue(0);
                    Red_LED.setValue(0);
                    Green_LED.setValue(0);
                }

            } else if (Potentiometer.getValue() < 520 && Button.getValue() == 1) {
                oLED.getCanvas().drawRect(75, 35, 30, 20, MonochromeCanvas.Color.BRIGHT);
                oLED.getCanvas().drawRect(0, 35, 30, 20, MonochromeCanvas.Color.DARK);
                oLED.display();
                Thread.sleep(2000);
                oLED.getCanvas().drawString(0, 20, "ALARM DISENGAGED");
                Buzzer.setValue(0);
                Red_LED.setValue(0);
                Green_LED.setValue(0);
                oLED.display();
            }

        }
    }
}

