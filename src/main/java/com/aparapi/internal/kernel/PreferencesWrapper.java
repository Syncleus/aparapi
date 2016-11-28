/**
 * Copyright (c) 2016 - 2017 Syncleus, Inc.
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
package com.aparapi.internal.kernel;

import com.aparapi.Kernel;

public class PreferencesWrapper {

  private Class<? extends Kernel> klass;
  private KernelPreferences preferences;

  public PreferencesWrapper(Class<? extends Kernel> klass, KernelPreferences preferences) {
    super();
    this.klass = klass;
    this.preferences = preferences;
  }

  public Class<? extends Kernel> getKernelClass() {
    return klass;
  }

  public KernelPreferences getPreferences() {
    return preferences;
  }
}

