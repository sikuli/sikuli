/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.ide;

import java.util.*;
import java.text.MessageFormat;

import org.sikuli.script.Debug;

public class I18N {
   static ResourceBundle i18nRB, i18nRB_en;
   static Locale curLocale;

   static {
      Locale locale = UserPreferences.getInstance().getLocale();
      setLocale(locale);
      Debug.info("locale: " + locale);
      Locale locale_en = new Locale("en","US");
      i18nRB_en = ResourceBundle.getBundle("i18n/IDE",locale_en);
   }

   public static void setLocale(Locale locale){
      curLocale = locale;
      try{
         i18nRB = ResourceBundle.getBundle("i18n/IDE",locale);
      }
      catch(MissingResourceException e){
         Debug.error("no locale for " + locale);
      }
   }

   public static String _I(String key, Object... args){ 
      String ret;
      if(i18nRB==null)
         ret = i18nRB_en.getString(key); 
      else{
         try {
            ret = i18nRB.getString(key);
         } catch (MissingResourceException e) {
            ret = i18nRB_en.getString(key); 
         } 
      }
      if(args.length>0){
         MessageFormat formatter = new MessageFormat("");
         formatter.setLocale(curLocale);
         formatter.applyPattern(ret);
         ret = formatter.format(args);
      }
      return ret;
   }

}
