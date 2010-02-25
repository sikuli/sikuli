#ifndef TIMING_BLOCK_H
#define TIMING_BLOCK_H

#include<string>
#include<iostream>
#include<sys/time.h>

#ifdef ENABLE_TIMING

class TimingBlock {
private:
   static int _depth;
   struct timeval _begin, _end;   
   std::string _name;
public:
   inline TimingBlock(std::string name){
      _name = name; 
      _depth++;
      gettimeofday(&_begin, NULL);
   }
   inline ~TimingBlock(){
      gettimeofday(&_end, NULL);
      _depth--;
      for(int i=0;i<_depth;i++)  std::cout << "  ";
      long begin = ((long)_begin.tv_sec*1000000)+_begin.tv_usec;
      long end = ((long)_end.tv_sec*1000000)+_end.tv_usec;
      std::cout << "[time] " << _name << " "
                << (end-begin)/1000.0 << "ms" << std::endl;
      if(_depth==0)   std::cout << std::endl;
   }
};

#else
class TimingBlock {
   TimingBlock(std::string name){ }
   ~TimingBlock();
}

#endif

#endif
