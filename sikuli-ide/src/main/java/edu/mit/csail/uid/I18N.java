package edu.mit.csail.uid;

import java.util.*;
import java.text.MessageFormat;

public class I18N {
   static ResourceBundle i18nRB, i18nRB_en;
   static Locale curLocale;

   static {
      Locale locale_en = new Locale("en","US");
      Locale locale = Locale.getDefault();
      curLocale = locale;
      Debug.info("locale: " + locale);
      i18nRB = ResourceBundle.getBundle("i18n/IDE",locale);
      i18nRB_en = ResourceBundle.getBundle("i18n/IDE",locale_en);
   }

   static String _I(String key, Object... args){ 
      String ret;
      try {
         //ret = new String (i18nRB.getString(key).getBytes("ISO-8859-1"),"UTF-8");
         ret = i18nRB.getString(key);
      } catch (MissingResourceException e) {
            ret = i18nRB_en.getString(key); 
      } /*catch (UnsupportedEncodingException e) {
         e.printStackTrace();
         return null;
      } */
      if(args.length>0){
         MessageFormat formatter = new MessageFormat("");
         formatter.setLocale(curLocale);
         formatter.applyPattern(ret);
         ret = formatter.format(args);
      }
      return ret;
   }

}
