from django.conf.urls import patterns, include, url
from hackaton_corruption import settings

# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
# admin.autodiscover()

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'hackaton_corruption.views.home', name='home'),
    # url(r'^hackaton_corruption/', include('hackaton_corruption.foo.urls')),

    # Uncomment the admin/doc line below to enable admin documentation:
    # url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    # url(r'^admin/', include(admin.site.urls)),
    url(r'^assets/(?P<path>.*)$', 'django.views.static.serve', {'document_root': settings.MEDIA_ROOT }),
    
    url(r'^$', 'hackaton_corruption.views.home', name='home'),
)
