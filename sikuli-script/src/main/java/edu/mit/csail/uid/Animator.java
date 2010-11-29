package edu.mit.csail.uid;

import java.util.Date;

public interface Animator {
   public float step();
   public boolean running();
}

class PulseAnimator implements Animator {
   protected float _v1, _v2;
   protected long _interval, _totalMS;
   protected boolean _running;
   protected long _begin_time = -1;

   public PulseAnimator(float v1, float v2, long interval, long totalMS){
      _v1 = v1;
      _v2 = v2;
      _interval = interval;
      _totalMS = totalMS;
      _running = true;

   }

   public float step(){
      if(_begin_time == -1){
         _begin_time = (new Date()).getTime();
         return _v1;
      }

      long now = (new Date()).getTime();
      long delta = now - _begin_time;
      if(delta >= _totalMS)
         _running = false;
      if( (delta/_interval) % 2 == 0 )
         return _v1;
      else
         return _v2;
   }

   public boolean running(){
      return _running;
   }
}

class LinearAnimator implements Animator{
   protected long _begin_time;
   protected float _beginVal, _endVal, _stepUnit;
   protected long _totalMS;
   protected boolean _running;

   public LinearAnimator(float beginVal, float endVal, long totalMS){
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
