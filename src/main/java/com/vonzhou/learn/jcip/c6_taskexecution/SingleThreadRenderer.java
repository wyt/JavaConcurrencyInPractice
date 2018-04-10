package com.vonzhou.learn.jcip.c6_taskexecution;

import java.util.ArrayList;
import java.util.List;

/**
 * SingleThreadRendere
 * <p/>
 * Rendering page elements sequentially 串行地渲染页面元素
 *
 * @author Brian Goetz and Tim Peierls
 */
public abstract class SingleThreadRenderer {

  void renderPage(CharSequence source) {
    renderText(source);
    List<ImageData> imageData = new ArrayList<ImageData>();
    for (ImageInfo imageInfo : scanForImageInfo(source))
      imageData.add(imageInfo.downloadImage());
    for (ImageData data : imageData)
      renderImage(data);
  }

  interface ImageData {
  }

  interface ImageInfo {
    ImageData downloadImage();
  }

  abstract void renderText(CharSequence s);

  abstract List<ImageInfo> scanForImageInfo(CharSequence s);

  abstract void renderImage(ImageData i);
}
