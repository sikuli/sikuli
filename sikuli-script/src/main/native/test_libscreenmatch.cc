#include <dlfcn.h>
#include <iostream>
#include "screenmatch.h"

using namespace std;

int main(int argc, char** argv){
  void *handle = dlopen("./libscreenmatch.so", RTLD_LAZY);
  Matches (*match)(const char*,const char*);
  match = (Matches (*)(const char*,const char*))dlsym(handle, "screen_match");
  if(!match){
     fprintf( stderr, "Can't load dynamic lib\n");
     return 1;
   }


  Matches matches;

  /* check for arguments */
  if( argc < 3 ) {
    fprintf( stderr, "Usage: screenmatch <full-desktop-image> <target-image>\n" );
    return 1;
  }

  matches = match(argv[2], argv[1]);
  for(Matches::iterator it = matches.begin(); it != matches.end(); ++it)
     cout << it->x << " " << it->y << " " << it->score << endl;

}
