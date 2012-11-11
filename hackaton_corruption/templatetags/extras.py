from hackaton_corruption import settings

register = template.Library()

@register.simple_tag
def get_path(which):
    return settings.MEDIA_PATHS[which.upper()]