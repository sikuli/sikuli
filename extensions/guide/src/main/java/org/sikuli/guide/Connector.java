/**
 * 
 */
package org.sikuli.guide;



public class Connector extends SikuliGuideArrow {

   private SikuliGuideComponent sourceComponent;
   private SikuliGuideComponent destinationComponent;

   public Connector(SikuliGuideComponent source, SikuliGuideComponent destination){
      super(source.getCenter(), destination.getCenter());
      sourceComponent = source;
      destinationComponent = destination;
   }      
   
   public void setSourceComponent(SikuliGuideComponent sourceComponent) {
      this.sourceComponent = sourceComponent;
      setSource(sourceComponent.getCenter());
      updateBounds();
   }

   public SikuliGuideComponent getSourceComponent() {
      return sourceComponent;
   }

   public void setDestinationComponent(SikuliGuideComponent destinationComponent) {
      this.destinationComponent = destinationComponent;
      setDestination(destinationComponent.getCenter());
      updateBounds();
   }

   public SikuliGuideComponent getDestinationComponent() {
      return destinationComponent;
   }
   
   public void update(SikuliGuideComponent comp){
      if (comp == getSourceComponent()){
         setSource(comp.getCenter());
      } else if (comp == getDestinationComponent()){
         setDestination(comp.getCenter());
      }
      updateBounds();
      repaint();
   }
}