#ifndef _FINDER_H_
#define _FINDER_H_

#include "pyramid-template-matcher.h"

class BaseFinder{
	
public:
	
	BaseFinder(IplImage* screen_image);
	BaseFinder(Mat source);
	BaseFinder(const char* source_image_filename);
	~BaseFinder();
	
	void setROI(int x, int y, int w, int h);
	
	int get_screen_height() const { return source.rows;};
	int get_screen_width()  const {return source.cols;};
	
	void find();
	
protected:
	
	Rect roi;
	
	Mat source;
	Mat roiSource;
   
   
   double min_similarity;
};

class WordFinder : public BaseFinder {
	
public:
	WordFinder(Mat source);
   static void train(Mat& trainingImage);
	
   void find(const char* word, double min_similarity);
   
   void find(vector<string> words, double min_similarity);
   
   bool hasNext();
   Match next();
   
   
private:
   vector<Match> matches;
   vector<Match>::iterator matches_iterator;
   
	void recognize(const Mat& inputImage);
	void test_find(const Mat& inputImage, const vector<string>& testWords);
};

class Finder : public BaseFinder{
	
public:
	
	Finder(Mat source);
	Finder(IplImage* source);
	Finder(const char* source_image_filename);
	~Finder();
	

	void find(Mat target, double min_similarity);
	void find(IplImage* target, double min_similarity);
	void find(const char *target_image_filename, double min_similarity);

	
	void find_all(Mat target, double min_similarity);
	void find_all(IplImage*  target, double min_similarity);
	void find_all(const char *target_image_filename, double min_similarity);  
	
	bool hasNext();
	Match next();
	
private:
	
   void create_matcher(Mat& source, Mat& target, int level, float ratio);
	PyramidTemplateMatcher* matcher;
	
	Match current_match;
	int current_rank;	
	
	// buffer matches and return top score
   void add_matches_to_buffer(int num_matches_to_add);
   float top_score_in_buffer();	
   
	vector<Match> buffered_matches;
   
   static int num_matchers;
   
   WordFinder *wf;
   
};



class FaceFinder : public BaseFinder {

public:

  FaceFinder(const char* screen_image_filename);
  ~FaceFinder();

  void find();
  bool hasNext();
  Match next();

private:

  CvMemStorage* storage;
  
  static CvHaarClassifierCascade* cascade;
  
  CvSeq* faces;
  int face_i;

};


class ChangeFinder : public BaseFinder {

public:

  ChangeFinder(const IplImage* screen_image);
  ChangeFinder(const Mat screen_image);
	
  ChangeFinder(const char* screen_image_filename);
  ~ChangeFinder();

  void find(IplImage* new_img);
  void find(Mat new_img);
	
  void find(const char* new_screen_image_filename);
  
  bool hasNext();
  Match next(); 

private:
  
  bool is_identical;

  IplImage *prev_img;
  CvSeq* c;
  CvMemStorage* storage;

};




#endif // _FINDER_H_
