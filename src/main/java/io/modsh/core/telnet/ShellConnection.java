package io.modsh.core.telnet;

import io.modsh.core.Handler;
import io.modsh.core.io.BinaryDecoder;
import io.modsh.core.io.BinaryEncoder;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class ShellConnection extends TelnetConnection {

  BinaryDecoder decoder;
  BinaryEncoder encoder;

  public ShellConnection(Handler<byte[]> output) {
    super(output);
  }

  @Override
  protected void onSendBinary(boolean binary) {
    super.onSendBinary(binary);
    if (binary) {
      encoder = new BinaryEncoder(TelnetConnection.UTF_8, new Handler<byte[]>() {
        @Override
        public void handle(byte[] event) {
          write(event);
        }
      });
    }
  }

  @Override
  protected void onReceiveBinary(boolean binary) {
    super.onReceiveBinary(binary);
    decoder = new BinaryDecoder(TelnetConnection.UTF_8, new Handler<int[]>() {
      @Override
      public void handle(int[] event) {
        for (int i : event) {
          onChar(i);
        }
      }
    });
  }

  @Override
  protected void onData(byte[] data) {
    if (decoder != null) {
      decoder.write(data);
    } else {
      // ???
      for (byte b : data) {
        onChar((char) b);
      }
    }
  }

  @Override
  protected void onOpen() {
    writeWillOption(Option.ECHO);
    writeWillOption(Option.SGA);
    writeDoOption(Option.NAWS);
    writeDoOption(Option.BINARY);
    writeWillOption(Option.BINARY);
    writeDoOption(Option.TERMINAL_TYPE);
  }

  protected void onChar(int c) {}

}