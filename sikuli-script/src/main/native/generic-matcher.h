#ifndef _GENERICMATCHER_H_
#define _GENERICMATCHER_H_

#include <vector>

using namespace std;

struct Match {
   int x, y;
   int w, h;
   double score;
   Match(){
    x=0;y=0;w=0;h=0;score=-1;
   }
   Match(int _x, int _y, int _w, int _h, double _score){
      x = _x; y = _y;
      w = _w; h = _h;
      score = _score;
   }
};

class GenericMatcher{
public:

  virtual Match next() = 0;

};

typedef std::vector<Match> Matches;


#endif // _GENERICMATCHER_H_