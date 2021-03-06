package com.vonzhou.learn.jcip.c6_taskexecution;

import static com.vonzhou.learn.jcip.buildingblocks.LaunderThrowable.launderThrowable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * 6-13.使用Future等待图像下载
 * <p/>
 * Waiting for image download with \Future
 *
 * @author Brian Goetz and Tim Peierls
 */
public abstract class FutureRenderer {

  private final ExecutorService executor = Executors.newCachedThreadPool();

  void renderPage(CharSequence source) {
    
    final List<ImageInfo> imageInfos = scanForImageInfo(source);

    // 图像下载任务
    Callable<List<ImageData>> task = new Callable<List<ImageData>>() {
      public List<ImageData> call() {
        List<ImageData> result = new ArrayList<ImageData>();
        for (ImageInfo imageInfo : imageInfos)
          result.add(imageInfo.downloadImage());
        return result;
      }
    };

    // 提交图像下载任务
    Future<List<ImageData>> future = executor.submit(task);
    
    // 渲染html标签
    renderText(source);

    try {
      List<ImageData> imageData = future.get();
      for (ImageData data : imageData)
        renderImage(data);
    } catch (InterruptedException e) {
      // Re-assert the thread's interrupted status
      Thread.currentThread().interrupt();
      // We don't need the result, so cancel the task too
      future.cancel(true);
    } catch (ExecutionException e) {
      throw launderThrowable(e.getCause());
    }
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
