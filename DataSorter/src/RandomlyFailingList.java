import java.util.*;

/**
 * Created by rishi on 2016-02-20.
 */
public class RandomlyFailingList<E> implements List<E> {
    private final ArrayList<E> elements;
    private final RandomlyFailingOperationWrapper operationWrapper;

    public RandomlyFailingList(double failureProbability) {
        elements = new ArrayList<>();
        operationWrapper = new RandomlyFailingOperationWrapper(failureProbability);
    }

    public RandomlyFailingList(Collection<? extends E> c, double failureProbability) {
        elements = new ArrayList<>(c);
        operationWrapper = new RandomlyFailingOperationWrapper(failureProbability);
    }

    public RandomlyFailingList(int initialCapacity, double failureProbability) {
        elements = new ArrayList<>(initialCapacity);
        operationWrapper = new RandomlyFailingOperationWrapper(failureProbability);
    }

    @Override
    public int size() {
        return operationWrapper.execute(new Operation<Integer>() {
            @Override
            public Integer execute() {
                return elements.size();
            }
        });
    }

    @Override
    public boolean isEmpty() {
        return operationWrapper.execute(new Operation<Boolean>() {
            @Override
            public Boolean execute() {
                return elements.isEmpty();
            }
        });
    }

    @Override
    public boolean contains(final Object o) {
        return operationWrapper.execute(new Operation<Boolean>() {
            @Override
            public Boolean execute() {
                return elements.contains(o);
            }
        });
    }

    @Override
    public Iterator<E> iterator() {
        return new RandomlyFailingIterator();
    }

    @Override
    public Object[] toArray() {
        // Can't fail this, as it's a primitive array
        return elements.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        // Can't fail this, as it's a primitive array
        return elements.toArray(a);
    }

    @Override
    public boolean add(final E e) {
        return operationWrapper.execute(new Operation<Boolean>() {
            @Override
            public Boolean execute() {
                return elements.add(e);
            }
        });
    }

    @Override
    public boolean remove(final Object o) {
        return operationWrapper.execute(new Operation<Boolean>() {
            @Override
            public Boolean execute() {
                return elements.remove(o);
            }
        });
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return operationWrapper.execute(new Operation<Boolean>() {
            @Override
            public Boolean execute() {
                return elements.containsAll(c);
            }
        });
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        return operationWrapper.execute(new Operation<Boolean>() {
            @Override
            public Boolean execute() {
                return elements.addAll(c);
            }
        });
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends E> c) {
        return operationWrapper.execute(new Operation<Boolean>() {
            @Override
            public Boolean execute() {
                return elements.addAll(index, c);
            }
        });
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return operationWrapper.execute(new Operation<Boolean>() {
            @Override
            public Boolean execute() {
                return elements.removeAll(c);
            }
        });
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return operationWrapper.execute(new Operation<Boolean>() {
            @Override
            public Boolean execute() {
                return elements.retainAll(c);
            }
        });
    }

    @Override
    public void clear() {
        operationWrapper.execute(new Operation<Void>() {
            @Override
            public Void execute() {
                elements.clear();
                return null;
            }
        });
    }

    @Override
    public E get(final int index) {
        return operationWrapper.execute(new Operation<E>() {
            @Override
            public E execute() {
                return elements.get(index);
            }
        });
    }

    @Override
    public E set(final int index, final E element) {
        return operationWrapper.execute(new Operation<E>() {
            @Override
            public E execute() {
                return elements.set(index, element);
            }
        });
    }

    @Override
    public void add(final int index, final E element) {
        operationWrapper.execute(new Operation<Void>() {
            @Override
            public Void execute() {
                elements.add(index, element);
                return null;
            }
        });
    }

    @Override
    public E remove(final int index) {
        return operationWrapper.execute(new Operation<E>() {
            @Override
            public E execute() {
                return elements.remove(index);
            }
        });
    }

    @Override
    public int indexOf(final Object o) {
        return operationWrapper.execute(new Operation<Integer>() {
            @Override
            public Integer execute() {
                return elements.indexOf(o);
            }
        });
    }

    @Override
    public int lastIndexOf(final Object o) {
        return operationWrapper.execute(new Operation<Integer>() {
            @Override
            public Integer execute() {
                return elements.lastIndexOf(o);
            }
        });
    }

    @Override
    public ListIterator<E> listIterator() {
        return new RandomlyFailingListIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new RandomlyFailingListIterator(index);
    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        return operationWrapper.execute(new Operation<List<E>>() {
            @Override
            public List<E> execute() {
                return elements.subList(fromIndex, toIndex);
            }
        });
    }

    private interface Operation<T> {
        T execute();
    }

    private static class RandomlyFailingOperationWrapper {
        private final double failureProbability;
        private long count = 0;

        public RandomlyFailingOperationWrapper(double failureProbability) {
            if (failureProbability > 1.0 || failureProbability < 0.0) {
                throw new IllegalArgumentException("failureProbability needs to be between 0 and 1.");
            }

            this.failureProbability = failureProbability;
        }

        public <T> T execute(Operation<T> operation) {
            ++count;
            if (shouldFail()) {
                throw new MemoryAccessFailureException("Random simulated failure event.");
            }

            return operation.execute();
        }

        private boolean shouldFail() {
            double hazard = count * failureProbability;
            Random rand = new Random();
            double randomEvent = rand.nextDouble();
            return randomEvent >= 0.5 && randomEvent <= 0.5 + hazard;
        }
    }

    private class RandomlyFailingIterator implements Iterator<E> {
        @Override
        public boolean hasNext() {
            return operationWrapper.execute(new Operation<Boolean>() {
                @Override
                public Boolean execute() {
                    return elements.iterator().hasNext();
                }
            });
        }

        @Override
        public E next() {
            return operationWrapper.execute(new Operation<E>() {
                @Override
                public E execute() {
                    return elements.iterator().next();
                }
            });
        }

        @Override
        public void remove() {
            operationWrapper.execute(new Operation<Void>() {
                @Override
                public Void execute() {
                    elements.iterator().remove();
                    return null;
                }
            });
        }
    }

    private class RandomlyFailingListIterator implements ListIterator<E> {

        private final int index;

        public RandomlyFailingListIterator(int index) {
            if (index < 0 || index > elements.size()) {
                throw new IndexOutOfBoundsException("Index: " + index);
            }
            this.index = index;
        }

        @Override
        public boolean hasNext() {
            return operationWrapper.execute(new Operation<Boolean>() {
                @Override
                public Boolean execute() {
                    return elements.listIterator(index).hasNext();
                }
            });
        }

        @Override
        public E next() {
            return operationWrapper.execute(new Operation<E>() {
                @Override
                public E execute() {
                    return elements.listIterator(index).next();
                }
            });
        }

        @Override
        public boolean hasPrevious() {
            return operationWrapper.execute(new Operation<Boolean>() {
                @Override
                public Boolean execute() {
                    return elements.listIterator(index).hasPrevious();
                }
            });
        }

        @Override
        public E previous() {
            return operationWrapper.execute(new Operation<E>() {
                @Override
                public E execute() {
                    return elements.listIterator(index).previous();
                }
            });
        }

        @Override
        public int nextIndex() {
            return operationWrapper.execute(new Operation<Integer>() {
                @Override
                public Integer execute() {
                    return elements.listIterator(index).nextIndex();
                }
            });
        }

        @Override
        public int previousIndex() {
            return operationWrapper.execute(new Operation<Integer>() {
                @Override
                public Integer execute() {
                    return elements.listIterator(index).previousIndex();
                }
            });
        }

        @Override
        public void remove() {
            operationWrapper.execute(new Operation<Void>() {
                @Override
                public Void execute() {
                    elements.listIterator(index).remove();
                    return null;
                }
            });
        }

        @Override
        public void set(final E e) {
            operationWrapper.execute(new Operation<Void>() {
                @Override
                public Void execute() {
                    elements.listIterator(index).set(e);
                    return null;
                }
            });
        }

        @Override
        public void add(final E e) {
            operationWrapper.execute(new Operation<Void>() {
                @Override
                public Void execute() {
                    elements.listIterator(index).add(e);
                    return null;
                }
            });
        }
    }
}