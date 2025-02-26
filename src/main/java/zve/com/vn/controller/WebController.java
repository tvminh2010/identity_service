package zve.com.vn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

  /* -------------------------------------------------------------- */
  @GetMapping("/")
  public String index() {
    return "index";
  }

  /* -------------------------------------------------------------- */
  @RequestMapping("/home")
  public String home() {
    return "home";
  }
  /* -------------------------------------------------------------- */
}
