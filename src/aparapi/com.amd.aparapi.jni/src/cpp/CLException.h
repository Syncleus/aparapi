#ifndef CL_EXCEPTION_H
#define CL_EXCEPTION_H

#include <string>
#include <stdio.h>
#include <exception>
#include "CLHelper.h"

class CLException : public std::exception {

private:
   int _status;
   std::string _message;

public:

   ~CLException() throw () {
   }

   CLException(int status, std::string message) {
      _status = status;
      _message = message;
   }

   CLException(const CLException& cle) {
      _status = cle._status;
      _message = cle._message;
   }

   CLException& operator=(const CLException& cle) {
      _status = cle._status;
      _message = cle._message;
      return *this;
   }

   int status() {
      return _status;
   }

   const char* message() {
      return _message.c_str();
   }

   void printError() {
      if(_message != "") {
         fprintf(stderr, "!!!!!!! %s failed %s\n", message(), CLHelper::errString(status()));
      }
   }

   const char* what() {
       return std::string("!!!!!!! " + _message + " failed " + CLHelper::errString(status()) + " \n").c_str();
   }

   static void checkCLError(cl_int status, std::string error) {
      if(status != CL_SUCCESS) {
         CLException(status, error).printError();
      }
   }
};


#endif // CL_EXCEPTION_H
