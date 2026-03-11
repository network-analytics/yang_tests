#ifndef LY_HELPER_H
#define LY_HELPER_H

#include <libyang/libyang.h>

struct ly_ctx *create_context(const char *yang_dir);
int load_module(struct ly_ctx *ctx, const char *module_name);
int validate_data(struct ly_ctx *ctx, const char *xml_data);

#endif