#include <cv.h>
#include <cxcore.h>
#include <highgui.h>
#include <iostream>
#include <sstream>
#include <vector>
#include <list>
#include <time.h>
#include <algorithm>

#include "screenmatch.h"
#include "template-matcher.h"
#include "ocr-matcher.h"

using namespace std;

// If both x,y,w,h are invalid (e.g., h=0), then it searches the entire image.

int main(int argc, char** argv){


  if (strcmp(argv[1],"ocr")== 0 ){
      Matches m = match_by_ocr(argv[2], // screen_image_filename
                   argv[3], // target_string
                   5,       // num_matches
                   0,       // min_similarity_threshold
                   0,       // x
                   0,       // y
                   200,     // w
                   200,     // h
                   true,    // write_images
                   true);   // display_images
      for(Matches::iterator it=m.begin();it!=m.end();++it){
         cout << it->x << " " << it->y << " " << it->score << endl;
      }

  }else if (strcmp(argv[1],"template") == 0 ){

      match_by_template(
                   argv[2], // screen_image_filename
                   argv[3], // template_image_filename
                   10,       // num_matches
                   0.7,       // min_similarity_threshold
                   false,  // search_multiscale
                   0,       // x
                   0,       // y
                   0,       // w
                   0,       // h
                   true,    // write_images
                   true);   // display_images
  }
}

