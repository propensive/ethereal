/*
    Ethereal, version [unreleased]. Copyright 2024 Jon Pretty, Propensive OÜ.

    The primary distribution site is: https://propensive.com/

    Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
    file except in compliance with the License. You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software distributed under the
    License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
    either express or implied. See the License for the specific language governing permissions
    and limitations under the License.
*/

package ethereal

import anticipation.*
import rudiments.*
import profanity.*

import language.experimental.pureFunctions

enum DaemonEvent:
  case Init
      (pid:         Pid,
       work:        Text,
       script:      Text,
       cliInput:    CliInput,
       arguments:   List[Text],
       environment: List[Text])

  case Trap(pid: Pid, signal: Signal)
  case Exit(pid: Pid)
  case Stderr(pid: Pid)

def service[BusType <: Matchable](using service: DaemonService[BusType]): DaemonService[BusType] = service