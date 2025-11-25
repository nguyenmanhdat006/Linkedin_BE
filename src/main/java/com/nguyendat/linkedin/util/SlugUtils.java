package com.nguyendat.linkedin.util;

import java.text.Normalizer;

public class SlugUtils {

    /**
     * Tạo slug từ text, hỗ trợ tiếng Việt
     * Ví dụ: "Nguyễn Đạt" → "nguyen-dat"
     */
    public static String generateSlug(String text) {
        if (text == null) return "";

        // 1. Chuyển về chữ thường
        String slug = text.toLowerCase();

        // 2. Loại bỏ dấu tiếng Việt
        slug = Normalizer.normalize(slug, Normalizer.Form.NFD);
        slug = slug.replaceAll("\\p{M}", "");

        // 3. Loại bỏ ký tự không phải chữ a-z, số 0-9, khoảng trắng hoặc dấu -
        slug = slug.replaceAll("[^a-z0-9\\s-]", "");

        // 4. Khoảng trắng → dấu -
        slug = slug.replaceAll("\\s+", "-");

        // 5. Nhiều dấu - liên tiếp → 1 dấu
        slug = slug.replaceAll("-+", "-");

        // 6. Bỏ - đầu và cuối
        slug = slug.replaceAll("^-|-$", "");

        return slug;
    }
}
