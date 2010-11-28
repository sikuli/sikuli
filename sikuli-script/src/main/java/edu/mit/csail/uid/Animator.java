package edu.mit.csail.uid;

import java.util.Date;

public class Animator {
   protected long _begin_time;
   protected float _beginVal, _endVal, _stepUnit;
   protected long _totalMS;
   protected boolean _running;

   public Animator(float beginVal, float endVal, long totalMS){
      _begin_time = -1;
      _beginVal = beginVal;
      _endVal = endVal;
      _totalMS = totalMS;
      _stepUnit = (endVal-beginVal)/(float)totalMS;
      _running = true;
   }

   // linear interpolation
   public float step(){
      if(_begin_time == -1){
         _begin_time = (new Date()).getTime();
         return _beginVal;
      }

      long now = (new Date()).getTime();
      long delta = now - _begin_time;
      float ret = _beginVal + _stepUnit * delta;
      if(_endVal>_beginVal){
         if(ret > _endVal){
            _running = false;
            ret = _endVal;
         }
      }
      else{
         if(ret < _endVal){
            _running = false;
            ret = _endVal;
         }
      }
      return ret;
   }

   public boolean running(){
      return _running;
   }
}
