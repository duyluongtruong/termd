/*
 * Copyright 2014 Julien Viet
 *
 * Julien Viet licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 */
package io.termd.core.telnet.netty;

import io.termd.core.Handler;
import io.termd.core.Provider;
import io.termd.core.readline.RequestContext;
import io.termd.core.telnet.TelnetBootstrap;
import io.termd.core.telnet.TelnetConnection;
import io.termd.core.telnet.TelnetHandler;
import io.termd.core.term.ReadlineTerm;

import java.util.concurrent.CountDownLatch;

/**
 * A test class.
 *
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class ReadlineBootstrap {

  public static final Handler<RequestContext> ECHO_HANDLER = new Handler<RequestContext>() {
    @Override
    public void handle(final RequestContext event) {
      new Thread() {
        @Override
        public void run() {
          new Thread() {
            @Override
            public void run() {
              try {
                Thread.sleep(3000);
              } catch (InterruptedException e) {
                e.printStackTrace();
              } finally {
//                event.write("You just typed :" + event.getRaw());
                event.end();
              }
            }
          }.start();
        }
      }.start();
    }
  };

  public static void main(String[] args) throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    new ReadlineBootstrap("localhost", 4000).start();
    latch.await();
  }

  private final TelnetBootstrap telnet;

  public ReadlineBootstrap(String host, int port) {
    this(new NettyTelnetBootstrap(host, port));
  }

  public ReadlineBootstrap(TelnetBootstrap telnet) {
    this.telnet = telnet;
  }

  public void start() {
    telnet.start(new Provider<TelnetHandler>() {
      @Override
      public TelnetHandler provide() {
        return new NettyTermConnection() {
          @Override
          protected void onOpen(TelnetConnection conn) {
            super.onOpen(conn);
            new ReadlineTerm(this, ECHO_HANDLER);
          }
        };
      }
    });
  }
}