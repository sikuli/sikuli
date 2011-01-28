package org.sikuli.script;

public class SikuliAction {
   
   // the region the action is originated from
   Region _source;

   // the target specified by the initiator of the action
   Object _target;
   
   // the captured screen image based on which this action is generated 
   ScreenImage _screenImage;

   // the match found by Sikuli on the screenImage
   Match _match;
   
   protected enum ActionType {CLICK, DOUBLE_CLICK, RIGHT_CLICK};
   
   ActionType _type;

   public <PSC> SikuliAction(ActionType type, Region source, PSC target, ScreenImage screenImage, Match match){
      _type = type;
      _source = source;
      _target = target;
      _match = match;
      _screenImage = screenImage;
   }

   public ActionType getType() {
      return _type;
   }

   public void setType(ActionType type){
      this._type = type;
   }
   
   public Object getTarget() {
      return _target;
   }

   public void setTarget(Object _target) {
      this._target = _target;
   }

   public ScreenImage getScreenImage() {
      return _screenImage;
   }

   public void setScreenImage(ScreenImage image) {
      _screenImage = image;
   }

   public Match getMatch() {
      return _match;
   }

   public void setMatch(Match _match) {
      this._match = _match;
   }
   
   public Region getSource() {
      return _source;
   }

   public void setSource(Region _source) {
      this._source = _source;
   }
}

