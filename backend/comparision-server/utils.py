import numpy as np
import math

def calculate_psnr(img1, img2):
    if img1.shape != img2.shape:
        return None
    mse = np.mean((img1 - img2) ** 2)
    if mse == 0:
        return None
    PIXEL_MAX = 255.0
    psnr = 20 * np.log10(PIXEL_MAX / np.sqrt(mse))
    if math.isinf(psnr) or math.isnan(psnr):
        return None
    return psnr

def calculate_ssim(img1, img2):
    if img1.shape != img2.shape:
        return None
    img1 = img1.astype(np.float64)
    img2 = img2.astype(np.float64)
    C1 = (0.01 * 255) ** 2
    C2 = (0.03 * 255) ** 2
    mu1 = img1.mean()
    mu2 = img2.mean()
    sigma1_sq = ((img1 - mu1) ** 2).mean()
    sigma2_sq = ((img2 - mu2) ** 2).mean()
    sigma12 = ((img1 - mu1) * (img2 - mu2)).mean()
    ssim = ((2 * mu1 * mu2 + C1) * (2 * sigma12 + C2)) / \
           ((mu1 ** 2 + mu2 ** 2 + C1) * (sigma1_sq + sigma2_sq + C2))
    if math.isinf(ssim) or math.isnan(ssim):
        return None
    return ssim
