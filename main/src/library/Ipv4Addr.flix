/*
 *  Copyright 2025 Cade Lueker
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

///
/// Represents a V4 Ip Address.
///
enum Ipv4Addr with Eq { // 32 bits
    case Ipv4Addr(Int8, Int8, Int8, Int8)
}

instance FromString[Ipv4Addr] {
    pub def fromString(x: String): Option[Ipv4Addr] = Ipv4Addr.fromString(x)
}

instance ToString[Ipv4Addr] {
    pub def toString(x: Ipv4Addr): String =
        let u8Conv = {b -> let b32 = Int8.toInt32(b); if (b32 < 0) b32 + 256 else b32};
        match x {
            case Ipv4Addr.Ipv4Addr(b1, b2, b3, b4) =>
                "${u8Conv(b1)}.${u8Conv(b2)}.${u8Conv(b3)}.${u8Conv(b4)}"
            case _ => ""
        }
}

mod Ipv4Addr {
    import java.net.InetAddress
    import java.net.UnknownHostException
    import java.lang.NumberFormatException

    use Ipv4Addr.Ipv4Addr

    def parseInt8NoLeading0(b: String): Option[Int8] =
        if (String.length(b) > 1 and String.startsWith({prefix = "0"}, b))
            None
        else
            forM(
                b32 <- Int32.fromString(b); if b32 < 256 and b32 > -256;
                b8 <- if (b32 > 127) Int32.tryToInt8(b32-256) else Int32.tryToInt8(b32)
            ) yield {
                b8
            }

    ///
    /// Attempts to parse the given String `s` as an `Ipv4Addr`.
    ///
    /// Returns `Some(addr)` if the string was successfully parsed. Otherwise returns `None`.
    ///
    pub def fromString(s: String): Option[Ipv4Addr] =
        let bytes = s
            |> String.split({regex = "[.]" })
            |> List.traverse(parseInt8NoLeading0);
        match bytes {
            case Some(b1::b2::b3::b4::Nil) => {
                Some(Ipv4Addr(b1, b2, b3, b4))
            }
            case _ => None
        }

}

