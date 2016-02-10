# -*- coding: utf-8 -*-
"""
Created on Thu Feb 11 06:59:27 2016

@author: nebula
"""


import sys
import qrcode
import openpyxl as opx
import urllib

input_filename = '/home/nebula/work/iventory_data/iventory20160209.xlsx'
output_dir = '/home/nebula/work/iventory_data/img/'

wb = opx.load_workbook(filename=input_filename)
ws = wb.worksheets[5]


for i in range(2,10):
    
    code_filename =  ws['B'+str(i)].value.replace(' ', '') + '.png'
    code_text = u'"KanzakiLabIventory", ' + '"' + ws['B'+str(i)].value + '", ' + '"' + ws['C'+str(i)].value + "\""
    print code_text
    img = qrcode.make(urllib.quote(code_text.encode('utf-8')))
    img.save(output_dir + code_filename)

