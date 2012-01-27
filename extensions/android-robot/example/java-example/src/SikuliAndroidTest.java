
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import org.sikuli.script.Settings;
import org.sikuli.script.ScreenImage;
import org.sikuli.script.android.AndroidScreen;
import com.android.monkeyrunner.MonkeyDevice;


public class SikuliAndroidTest {
   static AndroidScreen scr;
   
   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
      Settings.BundlePath = ".";
      scr = new AndroidScreen();
      assertNotNull(scr); 
   }

   @Test
   public void testCapture(){
      ScreenImage img = scr.capture();
      assertNotNull(img); 
      BufferedImage bimg = img.getImage();
      Rectangle bounds = scr.getBounds();
      assertEquals(bimg.getWidth(), bounds.getWidth());
      assertEquals(bimg.getHeight(), bounds.getHeight());
   }

   @Test
   public void testBounds(){
      Rectangle bound = scr.getBounds();
      assertNotNull(bound);
   }
   

   @Test
   public void testLockScreen(){
      MonkeyDevice dev = (MonkeyDevice)scr.getRobot().getDevice(); // still can use MonkeyDevice if needed.
      if(scr.exists("lock.png", 3) == null)
         dev.press("KEYCODE_POWER", MonkeyDevice.TouchPressType.DOWN_AND_UP); // press a special key POWER
      assertNotNull(scr.exists("lock.png", 10));
   }
}
