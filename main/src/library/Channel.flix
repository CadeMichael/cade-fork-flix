/*
 * Copyright 2022 Magnus Madsen
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

mod Channel {
    use Time.Duration

    ///
    /// Returns a new buffered channel with capacity for `n` elements.
    ///
    pub def buffered(n: Int32): (Sender[t], Receiver[t]) \ Chan =
        let m = if (n < 0) 0 else n;
        $CHANNEL_NEW$(m)

    ///
    /// Returns a new unbuffered channel (i.e. a channel with zero capacity).
    ///
    pub def unbuffered(): (Sender[t], Receiver[t]) \ Chan = buffered(0)

    ///
    /// Receives a message from the given channel `r`.
    ///
    /// Blocks until a message is dequeued.
    ///
    pub def recv(receiver: Receiver[t]): t \ {Chan, NonDet} = $CHANNEL_GET$(receiver)

    ///
    /// Sends the message `m` on the given channel `s`.
    ///
    /// Blocks until the message is enqueued.
    ///
    pub def send(m: t, sender: Sender[t]): Unit \ Chan = $CHANNEL_PUT$(sender, m)

    ///
    /// Returns a channel that receives the `Unit` message after duration `d`.
    ///
    pub def timeout(d: Duration): Receiver[Unit] \ {Chan, IO} =
        let (tx, rx) = Channel.buffered(1);
        spawn { Thread.sleep(d); send((), tx) } @ Static;
        rx

}
