/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
#ifndef TIMING_BLOCK_H
#define TIMING_BLOCK_H

#include<string>
#include<iostream>


#if !defined(_WIN32) && !defined(_WIN64) 
	#include<sys/time.h>
#else
	#include<time.h>
#endif

#ifdef ENABLE_TIMING

class TimingBlock {
private:
   static int _depth;
   std::string _name;
#if defined(_WIN32)
   SYSTEMTIME _begin;
   SYSTEMTIME _end;
#else
   struct timeval _begin, _end;   
#endif
public:
   inline TimingBlock(std::string name){
      _name = name; 
      _depth++;

#if defined(_WIN32)
      GetSystemTime(&_begin);
#else
      gettimeofday(&_begin, NULL);
#endif

   }
   inline ~TimingBlock(){
#if defined(_WIN32)
      GetSystemTime(&_end);
#else
      gettimeofday(&_end, NULL);
#endif
      _depth--;
      for(int i=0;i<_depth;i++)  std::cerr << "  ";
#if defined(_WIN32)
      long begin = (_begin.wSecond*1000000)+_begin.wMilliseconds;
      long end = (_end.wSecond*1000000)+_end.wMilliseconds;
#else
      long begin = ((long)_begin.tv_sec*1000000)+_begin.tv_usec;
      long end = ((long)_end.tv_sec*1000000)+_end.tv_usec;
#endif

      std::cerr << "[time] " << _name << " "
                << (end-begin)/1000.0 << "ms" << std::endl;
      if(_depth==0)   std::cerr << std::endl;
   }
};

#else
class TimingBlock {
public:
   inline TimingBlock(std::string name){ }
   inline ~TimingBlock(){}
};

#endif // #ifdef ENABLE_TIMING

#endif // #ifndef TIMING_BLOCK_H
