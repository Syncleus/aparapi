/**
 * Copyright (c) 2016 - 2018 Syncleus, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aparapi.exception;

import com.aparapi.internal.exception.AparapiException;

/**
 * This exception is thrown when an unexpected behavior occurs while querying the OpenCL platform. 
 * 
 * @author CoreRasurae
 */
@SuppressWarnings("serial")
public class QueryFailedException extends AparapiException {

   public QueryFailedException(String reason) {
      super(reason);
   }
   
   public QueryFailedException(String reason, Throwable _t) {
      super(reason, _t);
   }
   
   public QueryFailedException(Throwable _t) {
      super(_t);
   }
}
