INSERT INTO household_type(label) VALUES('administration');
INSERT INTO household_type(label) VALUES('appartement');
INSERT INTO household_type(label) VALUES('association');
INSERT INTO household_type(label) VALUES('bâtiment communal');
INSERT INTO household_type(label) VALUES('entreprise');
INSERT INTO household_type(label) VALUES('établissement scolaire');
INSERT INTO household_type(label) VALUES('maison d''habitation');
INSERT INTO household_type(label) VALUES('residence');

INSERT INTO taxpayer(name) VALUES('Pat Redway');
INSERT INTO taxpayer(name) VALUES('Mélanie Zetofrais');
INSERT INTO taxpayer(name) VALUES('Gérard Menfroi');
INSERT INTO taxpayer(name) VALUES('Lara Masset');
INSERT INTO taxpayer(name) VALUES('Camille Honnette');

INSERT INTO household(label, type_id, taxpayer_id) SELECT 'Famille Pat Redway', t.id, tp.id FROM household_type t JOIN taxpayer tp WHERE t.label='maison d''habitation' AND tp.name='Pat Redway';
INSERT INTO household(label, type_id, taxpayer_id) SELECT 'Famille Mélanie Zetofrais', t.id, tp.id FROM household_type t JOIN taxpayer tp WHERE t.label='appartement' AND tp.name='Mélanie Zetofrais';
INSERT INTO household(label, type_id, taxpayer_id) SELECT 'Association des réchauffés', t.id, tp.id FROM household_type t JOIN taxpayer tp WHERE t.label='association' AND tp.name='Gérard Menfroi';
INSERT INTO household(label, type_id, taxpayer_id) SELECT 'Roulotte Camille Honnette', t.id, tp.id FROM household_type t JOIN taxpayer tp WHERE t.label='entreprise' AND tp.name='Camille Honnette';
INSERT INTO household(label, type_id, taxpayer_id) SELECT 'Lara Masset', t.id, tp.id FROM household_type t JOIN taxpayer tp WHERE t.label='appartement' AND tp.name='Lara Masset';
INSERT INTO household(label, type_id, taxpayer_id) SELECT 'Famille Camille Honnette', t.id, tp.id FROM household_type t JOIN taxpayer tp WHERE t.label='appartement' AND tp.name='Camille Honnette';