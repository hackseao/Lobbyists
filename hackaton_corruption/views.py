# This Python file uses the following encoding: utf-8
from django.http import HttpResponse, HttpResponseServerError, HttpResponseRedirect, Http404
from django.core.cache import cache
from django.template.loader import get_template
from django.shortcuts import render_to_response
from django.template.loader import render_to_string
from django.template import Template, RequestContext
from django.conf import settings

def home(request):
    return render_to_response('index.html')
    
def search(request):
    return render_to_response('search/result.html')