#!/usr/bin/python3

import csv
with open('watt_house_profiles_year.csv', newline='') as f:
	reader = csv.reader(f,delimiter=';', quotechar='|')
	print('{', end='');
	for index,row in enumerate(reader):
		print(''.join(row[0:1]), end='')
		if(index != 525599):
			print(', ', end='')
		if(index % 10 == 0 and index != 0 ):
			print()
		if(index == 525599):
			break
	print('}')



