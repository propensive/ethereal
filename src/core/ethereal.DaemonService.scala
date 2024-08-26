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

import galilei.*
import anticipation.*
import rudiments.*
import exoskeleton.*

import language.experimental.pureFunctions

case class DaemonService[BusType <: Matchable]
    (pid:        Pid,
     shutdown:   () => Unit,
     cliInput:   CliInput,
     script:     Unix.Path,
     deliver:    BusType => Unit,
     bus:        LazyList[BusType],
     scriptName: Text)
extends ShellContext:

  def broadcast(message: BusType): Unit = deliver(message)