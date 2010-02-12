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
  SIKULI_EVENT_APPEAR,
  SIKULI_EVENT_VANISH,
  SIKULI_EVENT_CHANGE
};


class Observer{

public:

  Observer(int event_type, const char* param_image_filename, int handler_id, int x, int y, int w, int h);
  ~Observer();

  int event_type;
  IplImage* param_img;
  int x;
  int y;
  int h;
  int w;
  int handler_id;

  // if an event is still active, don't send another redundant event
  bool active;
};


class SikuliEventManager{

public:

  SikuliEventManager();
  ~SikuliEventManager();

  void addObserver(int event_type, const char* param_image_filename, int handler_id, int x, int y, int w, int h);

  vector<Event> update(const char* screen_image_filename);

  void setDebugMode(bool debug_mode);
  
private:
  
  vector<Observer*> observers;
  IplImage* prev_screen_image;


  bool debug_mode;
};

#endif // _EVENT_MANAGER_