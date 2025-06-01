import numpy as np  # For numerical operations
import math          # For checking infinity or NaN

# Function to calculate PSNR (Peak Signal-to-Noise Ratio) between two images
def calculate_psnr(img1, img2):
    if img1.shape != img2.shape:
        return None  # Return None if images are not the same size

    # Calculate Mean Squared Error (MSE)
    mse = np.mean((img1 - img2) ** 2)
    if mse == 0:
        return None  # If images are identical, PSNR is undefined

    PIXEL_MAX = 255.0  # Maximum pixel value for 8-bit images
    psnr = 20 * np.log10(PIXEL_MAX / np.sqrt(mse))  # PSNR formula

    # If result is infinite or NaN, return None
    if math.isinf(psnr) or math.isnan(psnr):
        return None

    return psnr  # Return PSNR value

# Function to calculate SSIM (Structural Similarity Index) between two images
def calculate_ssim(img1, img2):
    if img1.shape != img2.shape:
        return None  # Return None if image sizes do not match

    # Convert images to float for precision
    img1 = img1.astype(np.float64)
    img2 = img2.astype(np.float64)

    # Constants for stability (as per SSIM paper)
    C1 = (0.01 * 255) ** 2
    C2 = (0.03 * 255) ** 2

    # Compute means
    mu1 = img1.mean()
    mu2 = img2.mean()

    # Compute variances and covariance
    sigma1_sq = ((img1 - mu1) ** 2).mean()
    sigma2_sq = ((img2 - mu2) ** 2).mean()
    sigma12 = ((img1 - mu1) * (img2 - mu2)).mean()

    # SSIM formula
    ssim = ((2 * mu1 * mu2 + C1) * (2 * sigma12 + C2)) / \
           ((mu1 ** 2 + mu2 ** 2 + C1) * (sigma1_sq + sigma2_sq + C2))

    # If result is infinite or NaN, return None
    if math.isinf(ssim) or math.isnan(ssim):
        return None

    return ssim  # Return SSIM value
