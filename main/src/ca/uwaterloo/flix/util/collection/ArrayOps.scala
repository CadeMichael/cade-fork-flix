/*
 * Copyright 2024 Matthew Lutze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.uwaterloo.flix.util.collection

/**
  * Operations on arrays.
  */
object ArrayOps {

  /**
    * Returns the value at the given index in the array, if it is in-bounds.
    */
  def getOption[A](arr: Array[A], i: Int): Option[A] = {
    if (i >= 0 && i < arr.length) {
      Some(arr(i))
    } else {
      None
    }
  }
}
