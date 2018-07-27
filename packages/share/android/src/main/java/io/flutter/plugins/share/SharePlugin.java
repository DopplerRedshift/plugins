// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.share;

import android.content.Intent;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import java.util.Map;

/** Plugin method host for presenting a share sheet via Intent */
public class SharePlugin implements MethodChannel.MethodCallHandler {

  private static final String CHANNEL = "plugins.flutter.io/share";

  public static void registerWith(Registrar registrar) {
    MethodChannel channel = new MethodChannel(registrar.messenger(), CHANNEL);
    SharePlugin instance = new SharePlugin(registrar);
    channel.setMethodCallHandler(instance);
  }

  private final Registrar mRegistrar;

  private SharePlugin(Registrar registrar) {
    this.mRegistrar = registrar;
  }

  @Override
  public void onMethodCall(MethodCall call, MethodChannel.Result result) {
    if (call.method.equals("share")) {
      if (!(call.arguments instanceof Map)) {
        throw new IllegalArgumentException("Map argument expected");
      }
      // Android does not support showing the share sheet at a particular point on screen.
      share((String) call.argument("text"));
      result.success(null);
    } else {
      result.notImplemented();
    }
  }

  private void share(String text) {
      if (text == null || text.isEmpty()) {
          throw new IllegalArgumentException("Non-empty text expected");
      }

      Intent shareIntent = new Intent();
      shareIntent.setAction(Intent.ACTION_SEND);
      shareIntent.putExtra(Intent.EXTRA_TEXT, text);
      shareIntent.setType("text/plain");
      Intent chooserIntent = Intent.createChooser(shareIntent, null /* dialog title optional */);
      if (mRegistrar.activity() != null) {
          mRegistrar.activity().startActivity(chooserIntent);
      } else {
          chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          mRegistrar.context().startActivity(chooserIntent);
      }
  }

  private void share(String text, String type, String subject, String cc, String bcc) {
    
    Intent shareIntent = new Intent();
    shareIntent.setAction(Intent.ACTION_SEND);
    
    if (text == null || text.isEmpty()) {
      throw new IllegalArgumentException("Non-empty text expected");
    }

    if (type == null || type.isEmpty()) {
        throw new IllegalArgumentException("Non-empty type expected");
    }

    if (subject == null || subject.isEmpty()) {
        //shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        throw new IllegalArgumentException("Non-empty subject expected");
    }

    if (cc != null && !cc.isEmpty()) {
        shareIntent.putExtra(Intent.EXTRA_CC, cc);
    }

    if (bcc != null && !bcc.isEmpty()) {
        shareIntent.putExtra(Intent.EXTRA_BCC, bcc);
    }

    shareIntent.putExtra(Intent.EXTRA_TEXT, text);
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

    shareIntent.setType(type);
    Intent chooserIntent = Intent.createChooser(shareIntent, null /* dialog title optional */);

    if (mRegistrar.activity() != null) {
      mRegistrar.activity().startActivity(chooserIntent);
    } else {
      chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      mRegistrar.context().startActivity(chooserIntent);
    }
  }
}
