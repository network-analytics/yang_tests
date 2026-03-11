#include "ly_helper.h"
#include <stdio.h>

struct ly_ctx *create_context(const char *yang_dir) {
  struct ly_ctx *ctx = NULL;

  if (ly_ctx_new(yang_dir, 0, &ctx) != LY_SUCCESS) {
    fprintf(stderr, "Failed to create context\n");
    return NULL;
  }

  return ctx;
}

int load_module(struct ly_ctx *ctx, const char *module_name) {
  const struct lys_module *mod = ly_ctx_load_module(ctx, module_name, NULL, NULL);
  return mod ? 0 : -1;
}

int validate_data(struct ly_ctx *ctx, const char *xml_data) {
  struct lyd_node *data = NULL;

  if (lyd_parse_data_mem(ctx, xml_data,
                         LYD_XML,
                         LYD_PARSE_STRICT,
                         LYD_VALIDATE_PRESENT,
                         &data) != LY_SUCCESS) {
    return -1;
                         }

  lyd_free_all(data);
  return 0;
}