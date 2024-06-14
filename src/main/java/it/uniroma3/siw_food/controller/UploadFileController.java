package it.uniroma3.siw_food.controller;

import it.uniroma3.siw_food.service.UploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * This controller handles file upload functionality for the admin.
 */
@Controller
public class UploadFileController {

    @Autowired
    private UploadFileService uploadFileService;

    /**
     * Displays the file upload form.
     *
     * @return the view to upload a file
     */
    @GetMapping("/admin/uploadFile")
    public String showUploadForm() {
        return "uploadFileView";
    }

    /**
     * Handles the file upload.
     *
     * @param file               the file to be uploaded
     * @param redirectAttributes the attributes for the redirect URL
     * @return the redirect URL to the file upload form
     */
    @PostMapping("/admin/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        uploadFileService.store(file);
        redirectAttributes.addFlashAttribute("message", "File uploaded successfully!");
        return "redirect:/admin/uploadFile";
    }
}
