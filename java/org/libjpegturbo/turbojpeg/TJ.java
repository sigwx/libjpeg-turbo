/*
 * Copyright (C)2011-2013, 2017-2018, 2020-2024 D. R. Commander.
 *                                              All Rights Reserved.
 * Copyright (C)2015 Viktor Szathmáry.  All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * - Neither the name of the libjpeg-turbo Project nor the names of its
 *   contributors may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS",
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.libjpegturbo.turbojpeg;

import java.awt.Rectangle;

/**
 * TurboJPEG utility class (cannot be instantiated)
 */
public final class TJ {

  private TJ() {}

  /**
   * The number of chrominance subsampling options
   */
  public static final int NUMSAMP   = 7;
  /**
   * 4:4:4 chrominance subsampling (no chrominance subsampling)
   *
   * <p>The JPEG or YUV image will contain one chrominance component for every
   * pixel in the source image.
   */
  public static final int SAMP_444  = 0;
  /**
   * 4:2:2 chrominance subsampling
   *
   * <p>The JPEG or YUV image will contain one chrominance component for every
   * 2x1 block of pixels in the source image.
   */
  public static final int SAMP_422  = 1;
  /**
   * 4:2:0 chrominance subsampling
   *
   * <p>The JPEG or YUV image will contain one chrominance component for every
   * 2x2 block of pixels in the source image.
   */
  public static final int SAMP_420  = 2;
  /**
   * Grayscale
   *
   * <p>The JPEG or YUV image will contain no chrominance components.
   */
  public static final int SAMP_GRAY = 3;
  /**
   * 4:4:0 chrominance subsampling
   *
   * <p>The JPEG or YUV image will contain one chrominance component for every
   * 1x2 block of pixels in the source image.
   *
   * <p>NOTE: 4:4:0 subsampling is not fully accelerated in libjpeg-turbo.
   */
  public static final int SAMP_440  = 4;
  /**
   * 4:1:1 chrominance subsampling
   *
   * <p>The JPEG or YUV image will contain one chrominance component for every
   * 4x1 block of pixels in the source image.  All else being equal, a JPEG
   * image with 4:1:1 subsampling is almost exactly the same size as a JPEG
   * image with 4:2:0 subsampling, and in the aggregate, both subsampling
   * methods produce approximately the same perceptual quality.  However, 4:1:1
   * is better able to reproduce sharp horizontal features.
   *
   * <p>NOTE: 4:1:1 subsampling is not fully accelerated in libjpeg-turbo.
   */
  public static final int SAMP_411  = 5;
  /**
   * 4:4:1 chrominance subsampling
   *
   * <p>The JPEG or YUV image will contain one chrominance component for every
   * 1x4 block of pixels in the source image.  All else being equal, a JPEG
   * image with 4:4:1 subsampling is almost exactly the same size as a JPEG
   * image with 4:2:0 subsampling, and in the aggregate, both subsampling
   * methods produce approximately the same perceptual quality.  However, 4:4:1
   * is better able to reproduce sharp vertical features.
   *
   * <p>NOTE: 4:4:1 subsampling is not fully accelerated in libjpeg-turbo.
   */
  public static final int SAMP_441  = 6;
  /**
   * Unknown subsampling
   *
   * <p>The JPEG image uses an unusual type of chrominance subsampling.  Such
   * images can be decompressed into packed-pixel images, but they cannot be
   * <ul>
   * <li> decompressed into planar YUV images,
   * <li> losslessly transformed if {@link TJTransform#OPT_CROP} is specified
   * and {@link TJTransform#OPT_GRAY} is not specified,
   * or
   * <li> partially decompressed using a cropping region.
   * </ul>
   */
  public static final int SAMP_UNKNOWN = -1;

  /**
   * Returns the iMCU width for the given level of chrominance subsampling.
   *
   * <p>In a typical lossy JPEG image, 8x8 blocks of DCT coefficients for each
   * component are interleaved in a single scan.  If the image uses chrominance
   * subsampling, then multiple luminance blocks are stored together, followed
   * by a single block for each chrominance component.  The minimum set of
   * full-resolution luminance block(s) and corresponding (possibly subsampled)
   * chrominance blocks necessary to represent at least one DCT block per
   * component is called a "Minimum Coded Unit" or "MCU".  (For example, an MCU
   * in an interleaved lossy JPEG image that uses 4:2:2 subsampling consists of
   * two luminance blocks followed by one block for each chrominance
   * component.)  In a non-interleaved lossy JPEG image, each component is
   * stored in a separate scan, and an MCU is a single DCT block, so we use the
   * term "iMCU" (interleaved MCU) to refer to the equivalent of an MCU in an
   * interleaved JPEG image.  For the common case of interleaved JPEG images,
   * an iMCU is the same as an MCU.
   *
   * @param subsamp the level of chrominance subsampling (one of
   * {@link #SAMP_444 SAMP_*})
   *
   * @return the iMCU width for the given level of chrominance subsampling.
   */
  public static int getMCUWidth(int subsamp) {
    checkSubsampling(subsamp);
    return MCU_WIDTH[subsamp];
  }

  private static final int[] MCU_WIDTH = {
    8, 16, 16, 8, 8, 32, 8
  };


  /**
   * Returns the iMCU height for the given level of chrominance subsampling.
   *
   * <p>In a typical lossy JPEG image, 8x8 blocks of DCT coefficients for each
   * component are interleaved in a single scan.  If the image uses chrominance
   * subsampling, then multiple luminance blocks are stored together, followed
   * by a single block for each chrominance component.  The minimum set of
   * full-resolution luminance block(s) and corresponding (possibly subsampled)
   * chrominance blocks necessary to represent at least one DCT block per
   * component is called a "Minimum Coded Unit" or "MCU".  (For example, an MCU
   * in an interleaved lossy JPEG image that uses 4:2:2 subsampling consists of
   * two luminance blocks followed by one block for each chrominance
   * component.)  In a non-interleaved lossy JPEG image, each component is
   * stored in a separate scan, and an MCU is a single DCT block, so we use the
   * term "iMCU" (interleaved MCU) to refer to the equivalent of an MCU in an
   * interleaved JPEG image.  For the common case of interleaved JPEG images,
   * an iMCU is the same as an MCU.
   *
   * @param subsamp the level of chrominance subsampling (one of
   * {@link #SAMP_444 SAMP_*})
   *
   * @return the iMCU height for the given level of chrominance subsampling.
   */
  public static int getMCUHeight(int subsamp) {
    checkSubsampling(subsamp);
    return MCU_HEIGHT[subsamp];
  }

  private static final int[] MCU_HEIGHT = {
    8, 8, 16, 8, 16, 8, 32
  };


  /**
   * The number of pixel formats
   */
  public static final int NUMPF   = 12;
  /**
   * RGB pixel format
   *
   * <p>The red, green, and blue components in the image are stored in 3-sample
   * pixels in the order R, G, B from lowest to highest memory address within
   * each pixel.
   */
  public static final int PF_RGB  = 0;
  /**
   * BGR pixel format
   *
   * <p>The red, green, and blue components in the image are stored in 3-sample
   * pixels in the order B, G, R from lowest to highest memory address within
   * each pixel.
   */
  public static final int PF_BGR  = 1;
  /**
   * RGBX pixel format
   *
   * <p>The red, green, and blue components in the image are stored in 4-sample
   * pixels in the order R, G, B from lowest to highest memory address within
   * each pixel.  The X component is ignored when compressing/encoding and
   * undefined when decompressing/decoding.
   */
  public static final int PF_RGBX = 2;
  /**
   * BGRX pixel format
   *
   * <p>The red, green, and blue components in the image are stored in 4-sample
   * pixels in the order B, G, R from lowest to highest memory address within
   * each pixel.  The X component is ignored when compressing/encoding and
   * undefined when decompressing/decoding.
   */
  public static final int PF_BGRX = 3;
  /**
   * XBGR pixel format
   *
   * <p>The red, green, and blue components in the image are stored in 4-sample
   * pixels in the order R, G, B from highest to lowest memory address within
   * each pixel.  The X component is ignored when compressing/encoding and
   * undefined when decompressing/decoding.
   */
  public static final int PF_XBGR = 4;
  /**
   * XRGB pixel format
   *
   * <p>The red, green, and blue components in the image are stored in 4-sample
   * pixels in the order B, G, R from highest to lowest memory address within
   * each pixel.  The X component is ignored when compressing/encoding and
   * undefined when decompressing/decoding.
   */
  public static final int PF_XRGB = 5;
  /**
   * Grayscale pixel format
   *
   * Each 1-sample pixel represents a luminance (brightness) level from 0 to
   * the maximum sample value  (which is, for instance, 255 for 8-bit samples
   * or 4095 for 12-bit samples or 65535 for 16-bit samples.)
   */
  public static final int PF_GRAY = 6;
  /**
   * RGBA pixel format
   *
   * <p>This is the same as {@link #PF_RGBX}, except that when
   * decompressing/decoding, the X component is guaranteed to be equal to the
   * maximum sample value, which can be interpreted as an opaque alpha channel.
   */
  public static final int PF_RGBA = 7;
  /**
   * BGRA pixel format
   *
   * <p>This is the same as {@link #PF_BGRX}, except that when
   * decompressing/decoding, the X component is guaranteed to be equal to the
   * maximum sample value, which can be interpreted as an opaque alpha channel.
   */
  public static final int PF_BGRA = 8;
  /**
   * ABGR pixel format
   *
   * <p>This is the same as {@link #PF_XBGR}, except that when
   * decompressing/decoding, the X component is guaranteed to be equal to the
   * maximum sample value, which can be interpreted as an opaque alpha channel.
   */
  public static final int PF_ABGR = 9;
  /**
   * ARGB pixel format
   *
   * <p>This is the same as {@link #PF_XRGB}, except that when
   * decompressing/decoding, the X component is guaranteed to be equal to the
   * maximum sample value, which can be interpreted as an opaque alpha channel.
   */
  public static final int PF_ARGB = 10;
  /**
   * CMYK pixel format
   *
   * <p>Unlike RGB, which is an additive color model used primarily for
   * display, CMYK (Cyan/Magenta/Yellow/Key) is a subtractive color model used
   * primarily for printing.  In the CMYK color model, the value of each color
   * component typically corresponds to an amount of cyan, magenta, yellow, or
   * black ink that is applied to a white background.  In order to convert
   * between CMYK and RGB, it is necessary to use a color management system
   * (CMS.)  A CMS will attempt to map colors within the printer's gamut to
   * perceptually similar colors in the display's gamut and vice versa, but the
   * mapping is typically not 1:1 or reversible, nor can it be defined with a
   * simple formula.  Thus, such a conversion is out of scope for a codec
   * library.  However, the TurboJPEG API allows for compressing packed-pixel
   * CMYK images into YCCK JPEG images (see {@link #CS_YCCK}) and decompressing
   * YCCK JPEG images into packed-pixel CMYK images.
   */
  public static final int PF_CMYK = 11;
  /**
   * Unknown pixel format
   *
   * <p>Currently this is only used by
   * {@link TJCompressor#loadSourceImage TJCompressor.loadSourceImage()}.
   */
  public static final int PF_UNKNOWN = -1;


  /**
   * Returns the pixel size (in samples) for the given pixel format.
   *
   * @param pixelFormat the pixel format (one of {@link #PF_RGB PF_*})
   *
   * @return the pixel size (in samples) for the given pixel format.
   */
  public static int getPixelSize(int pixelFormat) {
    checkPixelFormat(pixelFormat);
    return PIXEL_SIZE[pixelFormat];
  }

  private static final int[] PIXEL_SIZE = {
    3, 3, 4, 4, 4, 4, 1, 4, 4, 4, 4, 4
  };


  /**
   * For the given pixel format, returns the number of samples that the red
   * component is offset from the start of the pixel.  For instance, if an
   * 8-bit-per-sample pixel of format <code>TJ.PF_BGRX</code> is stored in
   * <code>char pixel[]</code>, then the red component is
   * <code>pixel[TJ.getRedOffset(TJ.PF_BGRX)]</code>.
   *
   * @param pixelFormat the pixel format (one of {@link #PF_RGB PF_*})
   *
   * @return the red offset for the given pixel format, or -1 if the pixel
   * format does not have a red component.
   */
  public static int getRedOffset(int pixelFormat) {
    checkPixelFormat(pixelFormat);
    return RED_OFFSET[pixelFormat];
  }

  private static final int[] RED_OFFSET = {
    0, 2, 0, 2, 3, 1, -1, 0, 2, 3, 1, -1
  };


  /**
   * For the given pixel format, returns the number of samples that the green
   * component is offset from the start of the pixel.  For instance, if an
   * 8-bit-per-sample pixel of format <code>TJ.PF_BGRX</code> is stored in
   * <code>char pixel[]</code>, then the green component is
   * <code>pixel[TJ.getGreenOffset(TJ.PF_BGRX)]</code>.
   *
   * @param pixelFormat the pixel format (one of {@link #PF_RGB PF_*})
   *
   * @return the green offset for the given pixel format, or -1 if the pixel
   * format does not have a green component.
   */
  public static int getGreenOffset(int pixelFormat) {
    checkPixelFormat(pixelFormat);
    return GREEN_OFFSET[pixelFormat];
  }

  private static final int[] GREEN_OFFSET = {
    1, 1, 1, 1, 2, 2, -1, 1, 1, 2, 2, -1
  };


  /**
   * For the given pixel format, returns the number of samples that the blue
   * component is offset from the start of the pixel.  For instance, if an
   * 8-bit-per-sample pixel of format <code>TJ.PF_BGRX</code> is stored in
   * <code>char pixel[]</code>, then the blue component is
   * <code>pixel[TJ.getBlueOffset(TJ.PF_BGRX)]</code>.
   *
   * @param pixelFormat the pixel format (one of {@link #PF_RGB PF_*})
   *
   * @return the blue offset for the given pixel format, or -1 if the pixel
   * format does not have a blue component.
   */
  public static int getBlueOffset(int pixelFormat) {
    checkPixelFormat(pixelFormat);
    return BLUE_OFFSET[pixelFormat];
  }

  private static final int[] BLUE_OFFSET = {
    2, 0, 2, 0, 1, 3, -1, 2, 0, 1, 3, -1
  };


  /**
   * For the given pixel format, returns the number of samples that the alpha
   * component is offset from the start of the pixel.  For instance, if an
   * 8-bit-per-sample pixel of format <code>TJ.PF_BGRA</code> is stored in
   * <code>char pixel[]</code>, then the alpha component is
   * <code>pixel[TJ.getAlphaOffset(TJ.PF_BGRA)]</code>.
   *
   * @param pixelFormat the pixel format (one of {@link #PF_RGB PF_*})
   *
   * @return the alpha offset for the given pixel format, or -1 if the pixel
   * format does not have a alpha component.
   */
  public static int getAlphaOffset(int pixelFormat) {
    checkPixelFormat(pixelFormat);
    return ALPHA_OFFSET[pixelFormat];
  }

  private static final int[] ALPHA_OFFSET = {
    -1, -1, -1, -1, -1, -1, -1, 3, 3, 0, 0, -1
  };


  /**
   * The number of JPEG colorspaces
   */
  public static final int NUMCS = 5;
  /**
   * RGB colorspace
   *
   * <p>When generating the JPEG image, the R, G, and B components in the
   * source image are reordered into image planes, but no colorspace conversion
   * or subsampling is performed.  RGB JPEG images can be generated from and
   * decompressed to packed-pixel images with any of the extended RGB or
   * grayscale pixel formats, but they cannot be generated from or
   * decompressed to planar YUV images.
   */
  public static final int CS_RGB = 0;
  /**
   * YCbCr colorspace
   *
   * <p>YCbCr is not an absolute colorspace but rather a mathematical
   * transformation of RGB designed solely for storage and transmission.  YCbCr
   * images must be converted to RGB before they can be displayed.  In the
   * YCbCr colorspace, the Y (luminance) component represents the black &amp;
   * white portion of the original image, and the Cb and Cr (chrominance)
   * components represent the color portion of the original image.
   * Historically, the analog equivalent of this transformation allowed the
   * same signal to be displayed to both black &amp; white and color
   * televisions, but JPEG images use YCbCr primarily because it allows the
   * color data to be optionally subsampled in order to reduce network and disk
   * usage.  YCbCr is the most common JPEG colorspace, and YCbCr JPEG images
   * can be generated from and decompressed to packed-pixel images with any of
   * the extended RGB or grayscale pixel formats.  YCbCr JPEG images can also
   * be generated from and decompressed to planar YUV images.
   */
  @SuppressWarnings("checkstyle:ConstantName")
  public static final int CS_YCbCr = 1;
  /**
   * Grayscale colorspace
   *
   * <p>The JPEG image retains only the luminance data (Y component), and any
   * color data from the source image is discarded. Grayscale JPEG images can
   * be generated from and decompressed to packed-pixel images with any of the
   * extended RGB or grayscale pixel formats, or they can be generated from and
   * decompressed to planar YUV images.
   */
  public static final int CS_GRAY = 2;
  /**
   * CMYK colorspace
   *
   * <p>When generating the JPEG image, the C, M, Y, and K components in the
   * source image are reordered into image planes, but no colorspace conversion
   * or subsampling is performed.  CMYK JPEG images can only be generated from
   * and decompressed to packed-pixel images with the CMYK pixel format.
   */
  public static final int CS_CMYK = 3;
  /**
   * YCCK colorspace
   *
   * <p>YCCK (AKA "YCbCrK") is not an absolute colorspace but rather a
   * mathematical transformation of CMYK designed solely for storage and
   * transmission.  It is to CMYK as YCbCr is to RGB.  CMYK pixels can be
   * reversibly transformed into YCCK, and as with YCbCr, the chrominance
   * components in the YCCK pixels can be subsampled without incurring major
   * perceptual loss.  YCCK JPEG images can only be generated from and
   * decompressed to packed-pixel images with the CMYK pixel format.
   */
  public static final int CS_YCCK = 4;


  /**
   * Error handling behavior
   *
   * <p><b>Value</b>
   * <ul>
   * <li> <code>0</code> <i>[default]</i> Allow the current
   * compression/decompression/transform operation to complete unless a fatal
   * error is encountered.
   * <li> <code>1</code> Immediately discontinue the current
   * compression/decompression/transform operation if a warning (non-fatal
   * error) occurs.
   * </ul>
   */
  public static final int PARAM_STOPONWARNING = 0;
  /**
   * Row order in packed-pixel source/destination images
   *
   * <p><b>Value</b>
   * <ul>
   * <li> <code>0</code> <i>[default]</i> top-down (X11) order
   * <li> <code>1</code> bottom-up (Windows, OpenGL) order
   * </ul>
   */
  public static final int PARAM_BOTTOMUP = 1;
  /**
   * Perceptual quality of lossy JPEG images [compression only]
   *
   * <p><b>Value</b>
   * <ul>
   * <li> <code>1</code>-<code>100</code> (<code>1</code> = worst quality but
   * best compression, <code>100</code> = best quality but worst compression)
   * <i>[no default; must be explicitly specified]</i>
   * </ul>
   */
  public static final int PARAM_QUALITY = 3;
  /**
   * Chrominance subsampling level
   *
   * <p>The JPEG or YUV image uses (decompression, decoding) or will use (lossy
   * compression, encoding) the specified level of chrominance subsampling.
   *
   * <p>When pixels are converted from RGB to YCbCr (see {@link #CS_YCbCr}) or
   * from CMYK to YCCK (see {@link #CS_YCCK}) as part of the JPEG compression
   * process, some of the Cb and Cr (chrominance) components can be discarded
   * or averaged together to produce a smaller image with little perceptible
   * loss of image quality.  (The human eye is more sensitive to small changes
   * in brightness than to small changes in color.)  This is called
   * "chrominance subsampling".
   *
   * <p><b>Value</b>
   * <ul>
   * <li> One of {@link #SAMP_444 TJ.SAMP_*} <i>[no default; must be explicitly
   * specified for lossy compression, encoding, and decoding]</i>
   * </ul>
   */
  public static final int PARAM_SUBSAMP = 4;
  /**
   * JPEG width (in pixels) [decompression only, read-only]
   */
  public static final int PARAM_JPEGWIDTH = 5;
  /**
   * JPEG height (in pixels) [decompression only, read-only]
   */
  public static final int PARAM_JPEGHEIGHT = 6;
  /**
   * Data precision (bits per sample)
   *
   * <p>The JPEG image uses (decompression) or will use (lossless compression)
   * the specified number of bits per sample.  This parameter also specifies
   * the target data precision when loading a PBMPLUS file with
   * {@link TJCompressor#loadSourceImage TJCompressor.loadSourceImage()} and
   * the source data precision when saving a PBMPLUS file with
   * {@link TJDecompressor#saveImage TJDecompressor.saveImage()}.
   *
   * <p>The data precision is the number of bits in the maximum sample value,
   * which may not be the same as the width of the data type used to store the
   * sample.
   *
   * <p><b>Value</b>
   * <ul>
   * <li> <code>8</code> or <code>12</code> for lossy JPEG images;
   * <code>2</code> to <code>16</code> for lossless JPEG and PBMPLUS images
   * </ul>
   *
   * <p>12-bit JPEG data precision implies {@link #PARAM_OPTIMIZE} unless
   * {@link #PARAM_ARITHMETIC} is set.
   */
  public static final int PARAM_PRECISION = 7;
  /**
   * JPEG colorspace
   *
   * <p>The JPEG image uses (decompression) or will use (lossy compression) the
   * specified colorspace.
   *
   * <p><b>Value</b>
   * <ul>
   * <li> One of {@link #CS_RGB TJ.CS_*} <i>[default for lossy compression:
   * automatically selected based on the subsampling level and pixel
   * format]</i>
   * </ul>
   */
  public static final int PARAM_COLORSPACE = 8;
  /**
   * Chrominance upsampling algorithm [lossy decompression only]
   *
   * <p><b>Value</b>
   * <ul>
   * <li> <code>0</code> <i>[default]</i> Use smooth upsampling when
   * decompressing a JPEG image that was generated using chrominance
   * subsampling.  This creates a smooth transition between neighboring
   * chrominance components in order to reduce upsampling artifacts in the
   * decompressed image.
   * <li> <code>1</code> Use the fastest chrominance upsampling algorithm
   * available, which may combine upsampling with color conversion.
   * </ul>
   */
  public static final int PARAM_FASTUPSAMPLE = 9;
  /**
   * DCT/IDCT algorithm [lossy compression and decompression]
   *
   * <p><b>Value</b>
   * <ul>
   * <li> <code>0</code> <i>[default]</i> Use the most accurate DCT/IDCT
   * algorithm available.
   * <li> <code>1</code> Use the fastest DCT/IDCT algorithm available.
   * </ul>
   *
   * <p>This parameter is provided mainly for backward compatibility with
   * libjpeg, which historically implemented several different DCT/IDCT
   * algorithms because of performance limitations with 1990s CPUs.  In the
   * libjpeg-turbo implementation of the TurboJPEG API:
   *
   * <ul>
   * <li> The "fast" and "accurate" DCT/IDCT algorithms perform similarly on
   * modern x86/x86-64 CPUs that support AVX2 instructions.
   * <li> The "fast" algorithm is generally only about 5-15% faster than the
   * "accurate" algorithm on other types of CPUs.
   * <li> The difference in accuracy between the "fast" and "accurate"
   * algorithms is the most pronounced at JPEG quality levels above 90 and
   * tends to be more pronounced with decompression than with compression.
   * <li> For JPEG quality levels above 97, the "fast" algorithm degrades and
   * is not fully accelerated, so it is slower than the "accurate" algorithm.
   * </ul>
   */
  public static final int PARAM_FASTDCT = 10;
  /**
   * Huffman table optimization [lossy compression, lossless transformation]
   *
   * <p><b>Value</b>
   * <ul>
   * <li> <code>0</code> <i>[default]</i> The JPEG image will use the default
   * Huffman tables.
   * <li> <code>1</code> Optimal Huffman tables will be computed for the JPEG
   * image.  For lossless transformation, this can also be specified using
   * {@link TJTransform#OPT_OPTIMIZE}.
   * </ul>
   *
   * <p>Huffman table optimization improves compression slightly (generally 5%
   * or less), but it reduces compression performance considerably.
   */
  public static final int PARAM_OPTIMIZE = 11;
  /**
   * Progressive JPEG
   *
   * <p>In a progressive JPEG image, the DCT coefficients are split across
   * multiple "scans" of increasing quality.  Thus, a low-quality scan
   * containing the lowest-frequency DCT coefficients can be transmitted first
   * and refined with subsequent higher-quality scans containing
   * higher-frequency DCT coefficients.  When using Huffman entropy coding, the
   * progressive JPEG format also provides an "end-of-bands (EOB) run" feature
   * that allows large groups of zeroes, potentially spanning multiple MCUs, to
   * be represented using only a few bytes.
   *
   * <p><b>Value</b>
   * <ul>
   * <li> <code>0</code> <i>[default for compression, lossless
   * transformation]</i> The lossy JPEG image is (decompression) or will be
   * (compression, lossless transformation) single-scan.
   * <li> <code>1</code> The lossy JPEG image is (decompression) or will be
   * (compression, lossless transformation) progressive.  For lossless
   * transformation, this can also be specified using
   * {@link TJTransform#OPT_PROGRESSIVE}.
   * </ul>
   *
   * <p>Progressive JPEG images generally have better compression ratios than
   * single-scan JPEG images (much better if the image has large areas of solid
   * color), but progressive JPEG compression and decompression is considerably
   * slower than single-scan JPEG compression and decompression.  Can be
   * combined with {@link #PARAM_ARITHMETIC}.  Implies {@link #PARAM_OPTIMIZE}
   * unless {@link #PARAM_ARITHMETIC} is also set.
   */
  public static final int PARAM_PROGRESSIVE = 12;
  /**
   * Progressive JPEG scan limit for lossy JPEG images [decompression, lossless
   * transformation]
   *
   * <p>Setting this parameter causes the decompression and transform
   * operations to throw an error if the number of scans in a progressive JPEG
   * image exceeds the specified limit.  The primary purpose of this is to
   * allow security-critical applications to guard against an exploit of the
   * progressive JPEG format described in
   * <a href="https://libjpeg-turbo.org/pmwiki/uploads/About/TwoIssueswiththeJPEGStandard.pdf" target="_blank">this report</a>.
   *
   * <p><b>Value</b>
   * <ul>
   * <li> maximum number of progressive JPEG scans that the decompression and
   * transform operations will process <i>[default: <code>0</code> (no
   * limit)]</i>
   * </ul>
   *
   * @see #PARAM_PROGRESSIVE
   */
  public static final int PARAM_SCANLIMIT = 13;
  /**
   * Arithmetic entropy coding
   *
   * <p><b>Value</b>
   * <ul>
   * <li> <code>0</code> <i>[default for compression, lossless
   * transformation]</i> The lossy JPEG image uses (decompression) or will use
   * (compression, lossless transformation) Huffman entropy coding.
   * <li> <code>1</code> The lossy JPEG image uses (decompression) or will use
   * (compression, lossless transformation) arithmetic entropy coding.  For
   * lossless transformation, this can also be specified using
   * {@link TJTransform#OPT_ARITHMETIC}.
   * </ul>
   *
   * <p>Arithmetic entropy coding generally improves compression relative to
   * Huffman entropy coding, but it reduces compression and decompression
   * performance considerably.  Can be combined with
   * {@link #PARAM_PROGRESSIVE}.
   */
  public static final int PARAM_ARITHMETIC = 14;
  /**
   * Lossless JPEG
   *
   * <p><b>Value</b>
   * <ul>
   * <li> <code>0</code> <i>[default for compression]</i> The JPEG image is
   * (decompression) or will be (compression) lossy/DCT-based.
   * <li> <code>1</code> The JPEG image is (decompression) or will be
   * (compression) lossless/predictive.
   * </ul>
   *
   * <p>In most cases, lossless JPEG compression and decompression is
   * considerably slower than lossy JPEG compression and decompression, and
   * lossless JPEG images are much larger than lossy JPEG images.  Thus,
   * lossless JPEG images are typically used only for applications that require
   * mathematically lossless compression.  Also note that the following
   * features are not available with lossless JPEG images:
   * <ul>
   * <li> Colorspace conversion (lossless JPEG images always use
   * {@link #CS_RGB}, {@link #CS_GRAY}, or {@link #CS_CMYK}, depending on the
   * pixel format of the source image)
   * <li> Chrominance subsampling (lossless JPEG images always use
   * {@link #SAMP_444})
   * <li> JPEG quality selection
   * <li> DCT/IDCT algorithm selection
   * <li> Progressive JPEG
   * <li> Arithmetic entropy coding
   * <li> Compression from/decompression to planar YUV images
   * <li> Decompression scaling
   * <li> Lossless transformation
   * </ul>
   *
   * @see #PARAM_LOSSLESSPSV
   * @see #PARAM_LOSSLESSPT
   */
  public static final int PARAM_LOSSLESS = 15;
  /**
   * Lossless JPEG predictor selection value (PSV)
   *
   * <p><b>Value</b>
   * <ul>
   * <li> <code>1</code>-<code>7</code> <i>[default for compression:
   * <code>1</code>]</i>
   * </ul>
   *
   * <p>Lossless JPEG compression shares no algorithms with lossy JPEG
   * compression.  Instead, it uses differential pulse-code modulation (DPCM),
   * an algorithm whereby each sample is encoded as the difference between the
   * sample's value and a "predictor", which is based on the values of
   * neighboring samples.  If Ra is the sample immediately to the left of the
   * current sample, Rb is the sample immediately above the current sample, and
   * Rc is the sample diagonally to the left and above the current sample, then
   * the relationship between the predictor selection value and the predictor
   * is as follows:
   *
   * <table border=1>
   *   <caption></caption>
   *   <tr> <th>PSV</th> <th>Predictor</th> </tr>
   *   <tr> <td>1</td>   <td>Ra</td> </tr>
   *   <tr> <td>2</td>   <td>Rb</td> </tr>
   *   <tr> <td>3</td>   <td>Rc</td> </tr>
   *   <tr> <td>4</td>   <td>Ra + Rb – Rc</td> </tr>
   *   <tr> <td>5</td>   <td>Ra + (Rb – Rc) / 2</td> </tr>
   *   <tr> <td>6</td>   <td>Rb + (Ra – Rc) / 2</td> </tr>
   *   <tr> <td>7</td>   <td>(Ra + Rb) / 2</td> </tr>
   * </table>
   *
   * <p>Predictors 1-3 are 1-dimensional predictors, whereas Predictors 4-7 are
   * 2-dimensional predictors.  The best predictor for a particular image
   * depends on the image.
   *
   * @see #PARAM_LOSSLESS
   */
  public static final int PARAM_LOSSLESSPSV = 16;
  /**
   * Lossless JPEG point transform (Pt)
   *
   * <p><b>Value</b>
   * <ul>
   * <li> <code>0</code> through <i><b>precision</b> - 1</i>, where
   * <b><i>precision</i></b> is the JPEG data precision in bits <i>[default for
   * compression: <code>0</code>]</i>
   * </ul>
   *
   * <p>A point transform value of <code>0</code> is necessary in order to
   * generate a fully lossless JPEG image.  (A non-zero point transform value
   * right-shifts the input samples by the specified number of bits, which is
   * effectively a form of lossy color quantization.)
   *
   * @see #PARAM_LOSSLESS
   * @see #PARAM_PRECISION
   */
  public static final int PARAM_LOSSLESSPT = 17;
  /**
   * JPEG restart marker interval in MCUs [lossy compression only]
   *
   * <p>The nature of entropy coding is such that a corrupt JPEG image cannot
   * be decompressed beyond the point of corruption unless it contains restart
   * markers.  A restart marker stops and restarts the entropy coding algorithm
   * so that, if a JPEG image is corrupted, decompression can resume at the
   * next marker.  Thus, adding more restart markers improves the fault
   * tolerance of the JPEG image, but adding too many restart markers can
   * adversely affect the compression ratio and performance.
   *
   * <p>In typical JPEG images, an MCU (Minimum Coded Unit) is the minimum set
   * of interleaved "data units" (8x8 DCT blocks if the image is lossy or
   * samples if the image is lossless) necessary to represent at least one data
   * unit per component.  (For example, an MCU in an interleaved lossy JPEG
   * image that uses 4:2:2 subsampling consists of two luminance blocks
   * followed by one block for each chrominance component.)  In
   * single-component or non-interleaved JPEG images, an MCU is the same as a
   * data unit.
   *
   * <p><b>Value</b>
   * <ul>
   * <li> the number of MCUs between each restart marker <i>[default:
   * <code>0</code> (no restart markers)]</i>
   * </ul>
   *
   * <p> Setting this parameter to a non-zero value sets
   * {@link #PARAM_RESTARTROWS} to 0.
   */
  public static final int PARAM_RESTARTBLOCKS = 18;
  /**
   * JPEG restart marker interval in MCU rows [compression only]
   *
   * <p>See {@link #PARAM_RESTARTBLOCKS} for a description of restart markers
   * and MCUs.  An MCU row is a row of MCUs spanning the entire width of the
   * image.
   *
   * <p><b>Value</b>
   * <ul>
   * <li> the number of MCU rows between each restart marker <i>[default:
   * <code>0</code> (no restart markers)]</i>
   * </ul>
   *
   * <p>Setting this parameter to a non-zero value sets
   * {@link #PARAM_RESTARTBLOCKS} to 0.
   */
  public static final int PARAM_RESTARTROWS = 19;
  /**
   * JPEG horizontal pixel density
   *
   * <p><b>Value</b>
   * <ul>
   * <li> The JPEG image has (decompression) or will have (compression) the
   * specified horizontal pixel density <i>[default for compression:
   * <code>1</code>]</i>.
   * </ul>
   *
   * <p>This value is stored in or read from the JPEG header.  It does not
   * affect the contents of the JPEG image.  Note that this parameter is set by
   * {@link TJCompressor#loadSourceImage TJCompressor.loadSourceImage()} when
   * loading a Windows BMP file that contains pixel density information, and
   * the value of this parameter is stored to a Windows BMP file by
   * {@link TJDecompressor#saveImage TJDecompressor.saveImage()} if the value
   * of {@link #PARAM_DENSITYUNITS} is <code>2</code>.
   *
   * <p>This parameter has no effect unless the JPEG colorspace (see
   * {@link #PARAM_COLORSPACE}) is {@link #CS_YCbCr} or {@link #CS_GRAY}.
   *
   * @see #PARAM_DENSITYUNITS
   */
  public static final int PARAM_XDENSITY = 20;
  /**
   * JPEG vertical pixel density
   *
   * <p><b>Value</b>
   * <ul>
   * <li> The JPEG image has (decompression) or will have (compression) the
   * specified vertical pixel density <i>[default for compression:
   * <code>1</code>]</i>.
   * </ul>
   *
   * <p>This value is stored in or read from the JPEG header.  It does not
   * affect the contents of the JPEG image.  Note that this parameter is set by
   * {@link TJCompressor#loadSourceImage TJCompressor.loadSourceImage()} when
   * loading a Windows BMP file that contains pixel density information, and
   * the value of this parameter is stored to a Windows BMP file by
   * {@link TJDecompressor#saveImage TJDecompressor.saveImage()} if the value
   * of {@link #PARAM_DENSITYUNITS} is <code>2</code>.
   *
   * <p>This parameter has no effect unless the JPEG colorspace (see
   * {@link #PARAM_COLORSPACE}) is {@link #CS_YCbCr} or {@link #CS_GRAY}.
   *
   * @see #PARAM_DENSITYUNITS
   */
  public static final int PARAM_YDENSITY = 21;
  /**
   * JPEG pixel density units
   *
   * <p><b>Value</b>
   * <ul>
   * <li> <code>0</code> <i>[default for compression]</i> The pixel density of
   * the JPEG image is expressed (decompression) or will be expressed
   * (compression) in unknown units.
   * <li> <code>1</code> The pixel density of the JPEG image is expressed
   * (decompression) or will be expressed (compression) in units of
   * pixels/inch.
   * <li> <code>2</code> The pixel density of the JPEG image is expressed
   * (decompression) or will be expressed (compression) in units of pixels/cm.
   * </ul>
   *
   * <p>This value is stored in or read from the JPEG header.  It does not
   * affect the contents of the JPEG image.  Note that this parameter is set by
   * {@link TJCompressor#loadSourceImage TJCompressor.loadSourceImage()} when
   * loading a Windows BMP file that contains pixel density information, and
   * the value of this parameter is stored to a Windows BMP file by
   * {@link TJDecompressor#saveImage TJDecompressor.saveImage()} if the value
   * is <code>2</code>.
   *
   * <p>This parameter has no effect unless the JPEG colorspace (see
   * {@link #PARAM_COLORSPACE}) is {@link #CS_YCbCr} or {@link #CS_GRAY}.
   *
   * @see #PARAM_XDENSITY
   * @see #PARAM_YDENSITY
   */
  public static final int PARAM_DENSITYUNITS = 22;
  /**
   * Memory limit for intermediate buffers
   *
   * <p><b>Value</b>
   * <ul>
   * <li> the maximum amount of memory (in megabytes) that will be allocated
   * for intermediate buffers, which are used with progressive JPEG compression
   * and decompression, Huffman table optimization, lossless JPEG compression,
   * and lossless transformation <i>[default: <code>0</code> (no limit)]</i>
   * </ul>
   */
  public static final int PARAM_MAXMEMORY = 23;
  /**
   * Image size limit [decompression, lossless transformation]
   *
   * <p>Setting this parameter causes the decompression and transform
   * operations to throw an error if the number of pixels in the JPEG source
   * image exceeds the specified limit.  This allows security-critical
   * applications to guard against excessive memory consumption.
   *
   * <p><b>Value</b>
   * <ul>
   * <li> maximum number of pixels that the decompression and transform
   * operations will process <i>[default: <code>0</code> (no limit)]</i>
   * </ul>
   */
  public static final int PARAM_MAXPIXELS = 24;
  /**
   * Marker copying behavior [decompression, lossless transformation]
   *
   * <p><b>Value [lossless transformation]</b>
   * <ul>
   * <li> <code>0</code> Do not copy any extra markers (including comments,
   * JFIF thumbnails, Exif data, and ICC profile data) from the source image to
   * the destination image.
   * <li> <code>1</code> Do not copy any extra markers, except comment (COM)
   * markers, from the source image to the destination image.
   * <li> <code>2</code> <i>[default]</i> Copy all extra markers from the
   * source image to the destination image.
   * <li> <code>3</code> Copy all extra markers, except ICC profile data (APP2
   * markers), from the source image to the destination image.
   * <li> <code>4</code> Do not copy any extra markers, except ICC profile data
   * (APP2 markers), from the source image to the destination image.
   * </ul>
   *
   * <p>{@link TJTransform#OPT_COPYNONE} overrides this parameter for a
   * particular transform.  This parameter overrides any ICC profile that was
   * previously associated with a compressor instance using
   * {@link TJCompressor#setICCProfile TJCompressor.setICCProfile()} or with a
   * transformer instance using {@link TJTransformer#setICCProfile
   * TJTransformer.setICCProfile()}.
   *
   * <p>When decompressing, associating a JPEG source image with the
   * decompressor instance extracts the ICC profile from the source image if
   * this parameter is set to <code>2</code> or <code>4</code>.
   * {@link TJDecompressor#getICCProfile} can then be used to retrieve the
   * profile.
   */
  public static final int PARAM_SAVEMARKERS = 25;


  /**
   * The number of error codes
   */
  public static final int NUMERR = 2;
  /**
   * The error was non-fatal and recoverable, but the destination image may
   * still be corrupt.
   *
   * <p>NOTE: Due to the design of the TurboJPEG Java API, only certain methods
   * (specifically, {@link TJDecompressor TJDecompressor.decompress*()} methods
   * with a void return type) will complete and leave the destination image in
   * a fully recoverable state after a non-fatal error occurs.
   */
  public static final int ERR_WARNING = 0;
  /**
   * The error was fatal and non-recoverable.
   */
  public static final int ERR_FATAL = 1;


  /**
   * Returns the maximum size of the buffer (in bytes) required to hold a JPEG
   * image with the given width, height, and level of chrominance subsampling.
   *
   * @param width the width (in pixels) of the JPEG image
   *
   * @param height the height (in pixels) of the JPEG image
   *
   * @param jpegSubsamp the level of chrominance subsampling to be used when
   * generating the JPEG image (one of {@link #SAMP_444 TJ.SAMP_*}.)
   * {@link #SAMP_UNKNOWN} is treated like {@link #SAMP_444}, since a buffer
   * large enough to hold a JPEG image with no subsampling should also be large
   * enough to hold a JPEG image with an arbitrary level of subsampling.  Note
   * that lossless JPEG images always use {@link #SAMP_444}.
   *
   * @return the maximum size of the buffer (in bytes) required to hold a JPEG
   * image with the given width, height, and level of chrominance subsampling.
   */
  public static native int bufSize(int width, int height, int jpegSubsamp);

  /**
   * Returns the size of the buffer (in bytes) required to hold a unified
   * planar YUV image with the given width, height, and level of chrominance
   * subsampling.
   *
   * @param width the width (in pixels) of the YUV image
   *
   * @param align row alignment (in bytes) of the YUV image (must be a power of
   * 2.)  Setting this parameter to n specifies that each row in each plane of
   * the YUV image will be padded to the nearest multiple of n bytes
   * (1 = unpadded.)
   *
   * @param height the height (in pixels) of the YUV image
   *
   * @param subsamp the level of chrominance subsampling used in the YUV
   * image (one of {@link #SAMP_444 TJ.SAMP_*})
   *
   * @return the size of the buffer (in bytes) required to hold a unified
   * planar YUV image with the given width, height, and level of chrominance
   * subsampling.
   */
  public static native int bufSizeYUV(int width, int align, int height,
                                      int subsamp);

  /**
   * Returns the size of the buffer (in bytes) required to hold a YUV image
   * plane with the given parameters.
   *
   * @param componentID ID number of the image plane (0 = Y, 1 = U/Cb,
   * 2 = V/Cr)
   *
   * @param width width (in pixels) of the YUV image.  NOTE: This is the width
   * of the whole image, not the plane width.
   *
   * @param stride bytes per row in the image plane.
   *
   * @param height height (in pixels) of the YUV image.  NOTE: This is the
   * height of the whole image, not the plane height.
   *
   * @param subsamp the level of chrominance subsampling used in the YUV
   * image (one of {@link #SAMP_444 TJ.SAMP_*})
   *
   * @return the size of the buffer (in bytes) required to hold a YUV image
   * plane with the given parameters.
   */
  public static native int planeSizeYUV(int componentID, int width, int stride,
                                        int height, int subsamp);

  /**
   * Returns the plane width of a YUV image plane with the given parameters.
   * Refer to {@link YUVImage} for a description of plane width.
   *
   * @param componentID ID number of the image plane (0 = Y, 1 = U/Cb,
   * 2 = V/Cr)
   *
   * @param width width (in pixels) of the YUV image
   *
   * @param subsamp the level of chrominance subsampling used in the YUV image
   * (one of {@link #SAMP_444 TJ.SAMP_*})
   *
   * @return the plane width of a YUV image plane with the given parameters.
   */
  public static native int planeWidth(int componentID, int width, int subsamp);

  /**
   * Returns the plane height of a YUV image plane with the given parameters.
   * Refer to {@link YUVImage} for a description of plane height.
   *
   * @param componentID ID number of the image plane (0 = Y, 1 = U/Cb,
   * 2 = V/Cr)
   *
   * @param height height (in pixels) of the YUV image
   *
   * @param subsamp the level of chrominance subsampling used in the YUV image
   * (one of {@link #SAMP_444 TJ.SAMP_*})
   *
   * @return the plane height of a YUV image plane with the given parameters.
   */
  public static native int planeHeight(int componentID, int height,
                                       int subsamp);

  /**
   * Returns a list of fractional scaling factors that the JPEG decompressor
   * supports.
   *
   * @return a list of fractional scaling factors that the JPEG decompressor
   * supports.
   */
  public static native TJScalingFactor[] getScalingFactors();

  /**
   * A {@link TJScalingFactor} instance that specifies a scaling factor of 1/1
   * (no scaling)
   */
  public static final TJScalingFactor UNSCALED = new TJScalingFactor(1, 1);

  /**
   * A <code>java.awt.Rectangle</code> instance that specifies no cropping
   */
  public static final Rectangle UNCROPPED = new Rectangle(0, 0, 0, 0);

  static {
    TJLoader.load();
  }

  private static void checkPixelFormat(int pixelFormat) {
    if (pixelFormat < 0 || pixelFormat >= NUMPF)
      throw new IllegalArgumentException("Invalid pixel format");
  }

  private static void checkSubsampling(int subsamp) {
    if (subsamp < 0 || subsamp >= NUMSAMP)
      throw new IllegalArgumentException("Invalid subsampling type");
  }

}
