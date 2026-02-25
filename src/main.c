#include "ly_helper.h"
#include <stdio.h>

int main() {
  struct ly_ctx *ctx = create_context("../yang");
  if (!ctx) return 1;

  if (load_module(ctx, "example") != 0) {
    printf("Failed to load module\n");
    return 1;
  }

  const char *xml =
      "<system xmlns=\"urn:example\">"
      "<hostname>my-router</hostname>"
      "</system>";

  if (validate_data(ctx, xml) == 0) {
    printf("Data is valid!\n");
  } else {
    printf("Data validation failed!\n");
  }

  ly_ctx_destroy(ctx);
  return 0;
}