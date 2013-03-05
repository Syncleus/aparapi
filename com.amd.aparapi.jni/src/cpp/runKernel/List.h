/*
   Copyright (c) 2010-2011, Advanced Micro Devices, Inc.
   All rights reserved.

   Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
   following conditions are met:

   Redistributions of source code must retain the above copyright notice, this list of conditions and the following
   disclaimer. 

   Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution. 

   Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products
   derived from this software without specific prior written permission. 

   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
   INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
   DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
   SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
   SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
   WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
   OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

   If you use the software (in whole or in part), you shall adhere to all applicable U.S., European, and other export
   laws, including but not limited to the U.S. Export Administration Regulations ("EAR"), (15 C.F.R. Sections 730 
   through 774), and E.U. Council Regulation (EC) No 1334/2000 of 22 June 2000.  Further, pursuant to Section 740.6 of
   the EAR, you hereby certify that, except pursuant to a license granted by the United States Department of Commerce
   Bureau of Industry and Security or as otherwise permitted pursuant to a License Exception under the U.S. Export 
   Administration Regulations ("EAR"), you will not (1) export, re-export or release to a national of a country in 
   Country Groups D:1, E:1 or E:2 any restricted technology, software, or source code you receive hereunder, or (2) 
   export to Country Groups D:1, E:1 or E:2 the direct product of such technology or software, if such foreign produced
   direct product is subject to national security controls as identified on the Commerce Control List (currently 
   found in Supplement 1 to Part 774 of EAR).  For the most current Country Group listings, or for additional 
   information about the EAR or your obligations under those regulations, please refer to the U.S. Bureau of Industry
   and Security�s website at http://www.bis.doc.gov/. 
   */

#ifndef APARAPI_LIST_H
#define APARAPI_LIST_H

template <typename  T> class List; // forward

template <typename  T> class Ref{
   private:
      T value;
      int line;
      const char *fileName;
      Ref<T> *next;
      friend class List<T>;
   public:
      Ref(T _value, int _line, const char* _fileName);
};

template <typename  T> class List{
   private:
      const char *name;
      Ref<T> *head;
      int count;
   public:
      List(const char *_name);
      void add(T _value, int _line, const char *_fileName);
      void remove(T _value, int _line, const char *_fileName);
      void report(FILE *stream);
};

template <typename  T> Ref<T>::Ref(T _value, int _line, const char* _fileName):
   value(_value),
   line(_line),
   fileName(_fileName),
   next(NULL){
   }

template <typename  T> List<T>::List(const char *_name): 
   head(NULL),
   count(0),
   name(_name){
   }

template <typename  T> void List<T>::add(T _value, int _line, const char *_fileName){
   Ref<T> *handle = new Ref<T>(_value, _line, _fileName);
   handle->next = head; 
   head = handle;
   count++;
}

template <typename  T> void List<T>::remove(T _value, int _line, const char *_fileName){
   for (Ref<T> *ptr = head, *last=NULL; ptr != NULL; last=ptr, ptr = ptr->next){
      if (ptr->value == _value){
         if (last == NULL){ // head 
            head = ptr->next;
         }else{ // !head
            last->next = ptr->next;
         }
         delete ptr;
         count--;
         return;
      }
   }
   fprintf(stderr, "FILE %s LINE %d failed to find %s to remove %0lx\n", _fileName, _line, name, (unsigned long)_value);
}

template <typename  T> void List<T>::report(FILE *stream){
   if (head != NULL){
      fprintf(stream, "Resource report %d resources of type %s still in play ", count, name);
      for (Ref<T> *ptr = head; ptr != NULL; ptr = ptr->next){
         fprintf(stream, " %0lx(%d)", (unsigned long)ptr->value, ptr->line);
      }
      fprintf(stream, "\n");
   }
}


#ifdef CLHELPER_SOURCE
List<cl_command_queue> commandQueueList("cl_command_queue");
List<cl_mem> memList("cl_mem");
List<cl_event> readEventList("cl_event (read)");
List<cl_event> executeEventList("cl_event (exec)");
List<cl_event> writeEventList("cl_event (write)");
#else
extern List<cl_command_queue> commandQueueList;
extern List<cl_mem> memList;
extern List<cl_event> readEventList;
extern List<cl_event> executeEventList;
extern List<cl_event> writeEventList;
#endif

#endif // APARAPI_LIST_H

