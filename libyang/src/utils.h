
#ifndef UTILS_H
#define UTILS_H
#include <libyang/libyang.h>

char *read_file(const char *path);
struct lyd_node *parse_json(struct ly_ctx *ctx, const char *file, int flags, int valid_flags);

#endif
