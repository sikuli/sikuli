#ifndef _EVENT_MANAGER_
#define _EVENT_MANAGER_

#include <cv.h>
#include <cxcore.h>
#include <highgui.h>
#include <vector>

using namespace std;

struct Event{
  int type;
  int handler_id;
  int x;
  int y;
  int h;
  int w;
};

enum SIKULI_EVENT_TYPE{
  SIKULI_EVENT_APPEAR = 0,
  SIKULI_EVENT_VANISH = 1,
  SIKULI_EVENT_CHANGE = 2
};


class Observer{

public:

  Observer(int event_type, const char* param_image_filename, float similarity, 
           int handler_id, int x, int y, int w, int h);
  ~Observer();

  int event_type;
  IplImage* param_img;
  int x;
  int y;
  int h;
  int w;
  int handler_id;
  float similarity;

  // if an event is still active, don't send another redundant event
  bool active;
};


class SikuliEventManager{

public:

  SikuliEventManager();
  ~SikuliEventManager();

  void addObserver(int event_type, const char* param_image_filename, 
      float similarity, int handler_id, int x, int y, int w, int h);
  //void addObserver(int event_type, const IplImage* param_image, int handler_id, int x, int y, int w, int h);

  vector<Event> update(const char* screen_image_filename);
  vector<Event> update(const IplImage* screen_image);

  void setDebugMode(bool debug_mode);
  
private:
  
  vector<Observer*> observers;
  IplImage* prev_screen_image;


  bool debug_mode;
};

#endif // _EVENT_MANAGER_
