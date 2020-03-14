import csv

gene_info = '../../gene_info'
fields = []
with open(gene_info) as csv_file:
    csv_reader = csv.reader(csv_file, delimiter='\t')
    fields = next(csv_reader)
    print(fields)
    for row in csv_reader:
        print(row)
